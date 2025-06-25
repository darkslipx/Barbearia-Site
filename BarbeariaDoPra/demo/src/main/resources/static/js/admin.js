const API_URL = "http://localhost:8080";

// Carrega nome do usuário e opções de horário assim que a página é carregada
document.addEventListener('DOMContentLoaded', function () {
    const nome = localStorage.getItem('usuarioNome') || 'Usuário';
    document.getElementById('nome-usuario').textContent = nome;
    gerarOpcoesHorario24h("filtroHoraInicio");
});

// ---- AGENDAMENTOS ----

// Função principal para listar agendamentos filtrando por cliente, data e status
async function listarAgendamentos() {
    // Captura os filtros da tela
    const nomeCliente = document.getElementById('filtroCliente')?.value.trim() ?? '';
    const data = document.getElementById('filtroData')?.value ?? '';
    const status = document.getElementById('filtroStatus')?.value ?? '';

    // Monta a query string para GET (filtros)
    let query = [];
    if (nomeCliente) query.push(`cliente=${encodeURIComponent(nomeCliente)}`);
    if (data) query.push(`data=${encodeURIComponent(data)}`);
    if (status) query.push(`status=${encodeURIComponent(status)}`);
    let url = `${API_URL}/agendamentos`;
    if (query.length) url += "?" + query.join("&");

    // Chama a API e renderiza os dados na tabela
    const resp = await fetch(url);
    const dados = await resp.json();
    const tbody = document.getElementById('tbody-agendamentos');
    if (!tbody) return;
    tbody.innerHTML = '';

    // Monta cada linha da tabela de agendamentos
    dados.forEach(item => {
        tbody.innerHTML += `
            <tr>
                <td>${item.cliente?.pessoa?.nome ?? ''}</td>
                <td>${item.data ?? ''}</td>
                <td>${item.horario?.horaInicio ? item.horario.horaInicio.substring(0,5) : ''}</td>
                <td>${item.profissional?.pessoa?.nome ?? ''}</td>
                <td>${item.servico?.nome ?? ''}</td>
                <td class="status-${(item.status ?? '').toLowerCase()}">${item.status ?? ''}</td>
                <td>
                    <button class="btn btn-aceitar" onclick="atualizarStatusAgendamento(${item.idAgendamento}, 'CONFIRMADO', this)">Aceitar</button>
                    <button class="btn btn-rejeitar" onclick="atualizarStatusAgendamento(${item.idAgendamento}, 'RECUSADO', this)">Recusar</button>
                    <button class="btn btn-editar" onclick="mostrarDetalhesAgendamento(this)">Ver Detalhes</button>
                </td>
            </tr>
        `;
    });
}
window.listarAgendamentos = listarAgendamentos;

// Botão para aplicar filtros de agendamento
document.getElementById('btnFiltrarAgendamentos').onclick = function() {
    listarAgendamentos();
};

// Atualiza o status de um agendamento (CONFIRMADO ou RECUSADO)
window.atualizarStatusAgendamento = async function(idAgendamento, novoStatus, btn) {
    try {
        // Busca os dados completos do agendamento
        const resBusca = await fetch(`${API_URL}/agendamentos/${idAgendamento}`);
        if (!resBusca.ok) return alert('Erro ao buscar agendamento!');
        let agendamento = await resBusca.json();

        // Atualiza o status e envia ao backend via PUT
        agendamento.status = novoStatus;
        const res = await fetch(`${API_URL}/agendamentos/${idAgendamento}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(agendamento)
        });
        if (res.ok) {
            // Atualiza na tela sem recarregar tudo
            const tr = btn.closest('tr');
            const tdStatus = tr.querySelector('td[class^="status-"]');
            tdStatus.textContent = novoStatus;
            tdStatus.className = "status-" + novoStatus.toLowerCase();
        } else {
            alert("Erro ao atualizar status!");
        }
    } catch (e) {
        alert("Erro de conexão!");
    }
};

// Exibe o modal de detalhes de um agendamento selecionado
function mostrarDetalhesAgendamento(elemento) {
    const linha = elemento.closest('tr');
    const tds = linha.getElementsByTagName('td');
    document.getElementById('detalhe-cliente').textContent = tds[0].textContent;
    document.getElementById('detalhe-data').textContent = tds[1].textContent;
    document.getElementById('detalhe-horario').textContent = tds[2].textContent;
    document.getElementById('detalhe-profissional').textContent = tds[3].textContent;
    document.getElementById('detalhe-servico').textContent = tds[4].textContent;
    document.getElementById('detalhe-status').textContent = tds[5].textContent;
    document.getElementById('detalhes-agendamento').style.display = 'block';
}
window.mostrarDetalhesAgendamento = mostrarDetalhesAgendamento;

// Fecha o modal de detalhes do agendamento
function fecharDetalhes() {
    document.getElementById('detalhes-agendamento').style.display = 'none';
}
window.fecharDetalhes = fecharDetalhes;

// ---- CLIENTES ----
// Função para listar clientes com filtros por nome, email e telefone
async function listarClientes() {
    const nome = document.getElementById('filtroNomeCliente')?.value?.trim() ?? '';
    const email = document.getElementById('filtroEmailCliente')?.value?.trim() ?? '';
    const telefone = document.getElementById('filtroTelefoneCliente')?.value?.trim() ?? '';
    let url = `${API_URL}/clientes`;
    const params = [];
    if (nome) params.push(`nome=${encodeURIComponent(nome)}`);
    if (email) params.push(`email=${encodeURIComponent(email)}`);
    if (telefone) params.push(`telefone=${encodeURIComponent(telefone)}`);
    if (params.length) url += `?${params.join('&')}`;

    // Faz a requisição para a API e monta a tabela com os dados retornados
    const resp = await fetch(url);
    const dados = await resp.json();
    const tbody = document.getElementById('tbody-clientes');
    if (!tbody) return;
    tbody.innerHTML = '';
    dados.forEach(item => {
        tbody.innerHTML += `
            <tr>
                <td>${item.idCliente}</td>
                <td>${item.pessoa?.nome ?? ''}</td>
                <td>${item.pessoa?.email ?? ''}</td>
                <td>${item.telefone ?? ''}</td>
                <td>${item.dataNascimento ?? ''}</td>
                <td>
                    <button class="btn btn-editar" onclick="abrirModalEditarCliente(${item.idCliente})">Editar</button>
                    <button class="btn btn-rejeitar" onclick="removerCliente(${item.idCliente})">Remover</button>
                </td>
            </tr>
        `;
    });
}
window.listarClientes = listarClientes;

// Botão para filtrar clientes
document.getElementById('btnFiltrarClientes').onclick = listarClientes;

// -- MODAL EDIÇÃO CLIENTE --
// Função para abrir o modal de edição de cliente e preencher os campos com os dados
window.abrirModalEditarCliente = async function(idCliente) {
    const resp = await fetch(`${API_URL}/clientes/${idCliente}`);
    if (!resp.ok) return alert("Erro ao buscar cliente.");
    const c = await resp.json();

    document.getElementById('edit-idCliente').value = c.idCliente;
    document.getElementById('edit-nomeCliente').value = c.pessoa?.nome ?? '';
    document.getElementById('edit-emailCliente').value = c.pessoa?.email ?? '';
    document.getElementById('edit-telefoneCliente').value = c.telefone ?? '';
    document.getElementById('edit-nascCliente').value = c.dataNascimento ?? '';

    document.getElementById('modal-editar-cliente').style.display = 'flex';
}

// Fecha o modal e limpa o formulário de edição do cliente
window.fecharModalEditarCliente = function() {
    document.getElementById('modal-editar-cliente').style.display = 'none';
    document.getElementById('form-editar-cliente').reset();
}

// -- ENVIO FORMULÁRIO EDIÇÃO --
// Ao enviar o formulário de edição, faz PUT na API e atualiza a lista
document.getElementById('form-editar-cliente').onsubmit = async function(e) {
    e.preventDefault();
    const id = document.getElementById('edit-idCliente').value;
    const nome = document.getElementById('edit-nomeCliente').value.trim();
    const email = document.getElementById('edit-emailCliente').value.trim();
    const telefone = document.getElementById('edit-telefoneCliente').value.trim();
    const dataNascimento = document.getElementById('edit-nascCliente').value;

    // Buscar estrutura completa do cliente para editar apenas o necessário
    const resp = await fetch(`${API_URL}/clientes/${id}`);
    if (!resp.ok) return alert("Erro ao buscar cliente.");
    let cliente = await resp.json();
    cliente.pessoa = cliente.pessoa || {};
    cliente.pessoa.nome = nome;
    cliente.pessoa.email = email;
    cliente.telefone = telefone;
    cliente.dataNascimento = dataNascimento;

    const resp2 = await fetch(`${API_URL}/clientes/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cliente)
    });

    if (resp2.ok) {
        exibirMensagem("Cliente atualizado!", true);
        fecharModalEditarCliente();
        listarClientes();
    } else {
        exibirMensagem("Erro ao atualizar cliente!", false);
    }
};

// -- REMOVER CLIENTE --
// Função para remover cliente. Mostra um confirm e faz DELETE na API
window.removerCliente = async function(id) {
    if (!confirm("Deseja remover este cliente?")) return;
    try {
        const resp = await fetch(`${API_URL}/clientes/${id}`, { method: "DELETE" });
        if (resp.ok) {
            listarClientes();
            exibirMensagem('Cliente removido!', true);
        } else {
            exibirMensagem('Erro ao remover cliente!', false);
        }
    } catch {
        exibirMensagem('Erro de conexão!', false);
    }
}

// ---- PROFISSIONAIS (TABELA) ----
// Função para listar profissionais com filtros por nome, email e telefone
async function listarProfissionais() {
    const nome = document.getElementById('filtroNomeProf')?.value?.trim() ?? '';
    const email = document.getElementById('filtroEmailProf')?.value?.trim() ?? '';
    const telefone = document.getElementById('filtroTelefoneProf')?.value?.trim() ?? '';
    let url = `${API_URL}/profissional`;
    const params = [];
    if (nome) params.push(`nome=${encodeURIComponent(nome)}`);
    if (email) params.push(`email=${encodeURIComponent(email)}`);
    if (telefone) params.push(`telefone=${encodeURIComponent(telefone)}`);
    if (params.length) url += `?${params.join('&')}`;

    // Faz a requisição e monta a tabela
    const resp = await fetch(url);
    const dados = await resp.json();
    const tbody = document.getElementById('tbody-profissionais');
    if (!tbody) return;
    tbody.innerHTML = '';
    dados.forEach(item => {
        tbody.innerHTML += `
            <tr>
                <td>${item.idProfissional}</td>
                <td>${item.pessoa?.nome ?? ''}</td>
                <td>${item.pessoa?.email ?? ''}</td>
                <td>${item.telefone ?? ''}</td>
                <td>${item.dataNascimento ?? ''}</td>
                <td>
                    <button class="btn btn-editar" onclick="abrirModalEditarProf(${item.idProfissional})">Editar</button>
                    <button class="btn btn-rejeitar">Remover</button>
                </td>
            </tr>
        `;
    });
}
window.listarProfissionais = listarProfissionais;

// Botão para filtrar profissionais
document.getElementById('btnFiltrarProfissionais').onclick = listarProfissionais;

// ---- PROFISSIONAIS (SELECT) ----
// Preenche o select de profissionais para formulários/combos
async function preencherProfissionais() {
    const select = document.getElementById('profissional');
    if (!select) return; // Só executa se existir select na página
    const resp = await fetch(`${API_URL}/profissional`);
    const profissionais = await resp.json();
    select.innerHTML = '<option value="">Selecione um profissional</option>';
    profissionais.forEach(p => {
        select.innerHTML += `<option value="${p.idProfissional}">${p.pessoa?.nome ?? ''}</option>`;
    });
}
window.preencherProfissionais = preencherProfissionais;

// ---- HORÁRIOS ----
// Lista horários cadastrados, filtrando por profissional, dia, status, hora de início e fim
async function listarHorarios() {
    const profissional = document.getElementById('filtroProfissionalHorario')?.value.trim() ?? '';
    const dia = document.getElementById('filtroDiaHorario')?.value.trim() ?? '';
    const statusFiltro = document.getElementById('filtroStatusHorario')?.value.trim() ?? '';
    const horaInicio = document.getElementById('filtroHoraInicio')?.value ?? '';
    const horaFim = document.getElementById('filtroHoraFim')?.value ?? '';

    let query = [];
    if (profissional) query.push(`profissional=${encodeURIComponent(profissional)}`);
    if (dia) query.push(`diaSemana=${encodeURIComponent(dia)}`);
    if (statusFiltro === 'bloqueado') query.push(`bloqueado=true`);
    if (statusFiltro === 'disponivel') query.push(`bloqueado=false`);
    if (horaInicio) query.push(`horaInicio=${encodeURIComponent(horaInicio)}`);
    if (horaFim) query.push(`horaFim=${encodeURIComponent(horaFim)}`);

    let url = `${API_URL}/horario`;
    if (query.length) url += "?" + query.join("&");

    // Busca horários e preenche a tabela
    const resp = await fetch(url);
    const dados = await resp.json();
    const tbody = document.getElementById('tbody-horarios');
    if (!tbody) return;
    tbody.innerHTML = '';

    // Monta cada linha, já com botão de bloquear/desbloquear
    dados.forEach(item => {
        const bloqueado = !!item.bloqueado;
        let statusLabel = bloqueado ? 'Bloqueado' : 'Disponível';
        let statusClass = bloqueado ? 'status-bloqueado' : 'status-disponivel';
        let btnClass = bloqueado ? 'btn-aceitar' : 'btn-rejeitar';
        let btnLabel = bloqueado ? 'Desbloquear' : 'Bloquear';

        tbody.innerHTML += `
            <tr>
                <td>${item.profissional?.pessoa?.nome ?? ''}</td>
                <td>${item.diaSemana ?? ''}</td>
                <td>${item.horaInicio?.substring(0,5) ?? ''}</td>
                <td>${item.horaFim?.substring(0,5) ?? ''}</td>
                <td class="${statusClass}">${statusLabel}</td>
                <td>
                    <button class="btn ${btnClass}" onclick="toggleBloqueioHorario(${item.idHorario}, ${bloqueado})">
                        ${btnLabel}
                    </button>
                </td>
            </tr>
        `;
    });
}
window.listarHorarios = listarHorarios;


// Gera opções de horários no formato 24h para um <select>
// Permite também adicionar horários "extras" caso existam horários fora do padrão
function gerarOpcoesHorario24h(selectId, horariosExtras = []) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = `<option value="">Início</option>`;
    let horariosSet = new Set();

    // Gera horários de 00:00 até 23:30, de 30 em 30min
    for (let h = 0; h < 24; h++) {
        for (let m = 0; m < 60; m += 30) {
            horariosSet.add(`${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`);
        }
    }

    // Adiciona horários extras, se existirem
    horariosExtras.forEach(h => horariosSet.add(h));

    // Ordena horários
    let horariosOrdenados = Array.from(horariosSet).sort();

    // Preenche o <select> com as opções
    horariosOrdenados.forEach(val => {
        select.innerHTML += `<option value="${val}">${val}</option>`;
    });
}

// Função para bloquear ou desbloquear um horário
window.toggleBloqueioHorario = async function(idHorario, bloqueado) {
    let endpoint = bloqueado ? 'desbloquear' : 'bloquear';
    try {
        const resp = await fetch(`${API_URL}/horario/${idHorario}/${endpoint}`, { method: 'PUT' });
        if (resp.ok) {
            listarHorarios();
            exibirMensagem(bloqueado ? 'Horário desbloqueado!' : 'Horário bloqueado!', true);
        } else {
            exibirMensagem('Erro ao alterar status do horário!', false);
        }
    } catch {
        exibirMensagem('Erro de conexão!', false);
    }
};

// Exibe modal com detalhes do horário selecionado na tabela
function mostrarDetalhesHorario(elemento) {
    const linha = elemento.closest('tr');
    const tds = linha.getElementsByTagName('td');
    document.getElementById('detalhe-prof-horario').textContent = tds[0].textContent;
    document.getElementById('detalhe-dia-horario').textContent = tds[1].textContent;
    document.getElementById('detalhe-inicio-horario').textContent = tds[2].textContent;
    document.getElementById('detalhe-fim-horario').textContent = tds[3].textContent;
    document.getElementById('modal-detalhes-horario').style.display = 'flex';
    document.body.classList.add('modal-aberto');
}
window.mostrarDetalhesHorario = mostrarDetalhesHorario;

// Fecha o modal de detalhes do horário
function fecharModalDetalhesHorario() {
    document.getElementById('modal-detalhes-horario').style.display = 'none';
    document.body.classList.remove('modal-aberto');
}
window.fecharModalDetalhesHorario = fecharModalDetalhesHorario;

// ---- ABAS ----
// Função para alternar entre abas no sistema administrativo
// Persiste a aba ativa no localStorage e executa a listagem correspondente
window.openTab = function(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    const aba = document.getElementById(tabId);
    if (aba) aba.classList.add('active');
    document.querySelectorAll('.tab-btn').forEach(btn => {
        if (btn.getAttribute('onclick')?.includes(`openTab('${tabId}')`)) {
            btn.classList.add('active');
        }
    });

    // Salva a aba ativa no localStorage
    localStorage.setItem('adminAbaAtiva', tabId);

    // Executa listagem ao abrir aba
    if (tabId === 'agendamentos') listarAgendamentos();
    if (tabId === 'clientes') listarClientes();
    if (tabId === 'profissionais') listarProfissionais();
    if (tabId === 'horarios') listarHorarios();
    if (tabId === 'funcionamento') {
        preencherProfissionaisFuncionamento();
        listarFuncionamentos();
    }
    if (tabId === 'servicos') listarServicos();
};

// ---- INICIALIZAÇÃO ----
// Ao carregar o DOM, restaura a última aba aberta e preenche selects de profissionais
document.addEventListener('DOMContentLoaded', () => {
    const abaSalva = localStorage.getItem('adminAbaAtiva') || 'agendamentos';
    openTab(abaSalva);
    preencherProfissionais(); // Preenche select se existir
});

// ---- FUNCIONAMENTO ----

// Lista todos os funcionamentos cadastrados e preenche a tabela
async function listarFuncionamentos() {
    const tbody = document.getElementById('funcionamento-tbody');
    if (!tbody) return;
    const resp = await fetch(`${API_URL}/funcionamento`);
    const dados = await resp.json();
    tbody.innerHTML = '';
    if (!dados.length) {
        tbody.innerHTML = `<tr><td colspan="5">Nenhuma disponibilidade cadastrada.</td></tr>`;
        return;
    }
    dados.forEach(item => {
        tbody.innerHTML += `
            <tr>
                <td>${item.profissional?.pessoa?.nome ?? ''}</td>
                <td>${item.diaSemana ?? ''}</td>
                <td>${item.horaInicio ?? ''}</td>
                <td>${item.horaFim ?? ''}</td>
                <td>
                    <button class="btn btn-rejeitar" onclick="abrirModalExcluirFuncionamento(${item.id})">Excluir</button>
                    <button class="btn btn-gerar" onclick="gerarHorariosFuncionamento(${item.id})">Gerar horários</button>
                </td>
            </tr>
        `;
    });
}
window.listarFuncionamentos = listarFuncionamentos;

// Chama endpoint para gerar horários automáticos a partir do funcionamento
window.gerarHorariosFuncionamento = async function(idFuncionamento) {
    const intervalo = 30; // Sempre gera de 30 em 30 minutos
    if (!idFuncionamento) {
        alert("ID de funcionamento inválido!");
        return;
    }
    if (!confirm(`Gerar horários de ${intervalo} minutos para este funcionamento?`)) return;
    try {
        const resp = await fetch(`${API_URL}/funcionamento/${idFuncionamento}/gerar-horarios?intervalo=${intervalo}`, {
            method: 'POST'
        });
        const text = await resp.text();
        if (resp.ok) {
            exibirMensagem('Horários gerados com sucesso!', true);
            listarHorarios();
        } else {
            exibirMensagem(text || 'Erro ao gerar horários!', false);
        }
    } catch {
        exibirMensagem('Erro de conexão!', false);
    }
};

// Preenche o select de profissionais na tela de funcionamento
async function preencherProfissionaisFuncionamento() {
    const select = document.getElementById('profissional');
    if (!select) return;
    const resp = await fetch(`${API_URL}/profissional`);
    const profissionais = await resp.json();
    select.innerHTML = '<option value="">Selecione um profissional</option>';
    profissionais.forEach(p => {
        select.innerHTML += `<option value="${p.idProfissional}">${p.pessoa?.nome ?? ''}</option>`;
    });
}
window.preencherProfissionaisFuncionamento = preencherProfissionaisFuncionamento;

// Cadastro de funcionamento: envia dados para a API e atualiza a lista ao cadastrar
const formFunc = document.getElementById('form-funcionamento');
if (formFunc) {
    formFunc.onsubmit = async function(e) {
        e.preventDefault();
        const profissionalId = document.getElementById('profissional').value;
        const diaSemana = document.getElementById('func-dia').value;
        const horaInicio = document.getElementById('func-horaInicio').value;
        const horaFim = document.getElementById('func-horaFim').value;
        try {
            const resp = await fetch(`${API_URL}/funcionamento`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    profissional: { idProfissional: Number(profissionalId) },
                    diaSemana,
                    horarioDisponivel: `${horaInicio} - ${horaFim}`,
                    horaInicio,
                    horaFim
                })
            });
            if (!resp.ok) throw new Error('Erro ao cadastrar disponibilidade!');
            formFunc.reset();
            listarFuncionamentos();
            exibirMensagem('Disponibilidade cadastrada com sucesso!', true);
        } catch {
            exibirMensagem('Erro ao cadastrar disponibilidade!', false);
        }
    }
}

// Controle do modal de exclusão de funcionamento
let idFuncExcluir = null;
window.abrirModalExcluirFuncionamento = function(id) {
    idFuncExcluir = id;
    document.getElementById('modal-excluir-func').style.display = 'block';
};
const btnConfirmaExcluir = document.getElementById('btn-confirmar-excluir-func');
if (btnConfirmaExcluir) {
    btnConfirmaExcluir.onclick = async function() {
        if (idFuncExcluir) {
            await fetch(`${API_URL}/funcionamento/${idFuncExcluir}`, { method: 'DELETE' });
            document.getElementById('modal-excluir-func').style.display = 'none';
            listarFuncionamentos();
            exibirMensagem('Disponibilidade excluída!', true);
        }
    };
}

// Exibe um alerta temporário na tela
function exibirMensagem(msg, sucesso) {
    const alerta = document.getElementById('alerta');
    if (alerta) {
        alerta.textContent = msg;
        alerta.style.background = sucesso ? '#4caf50' : '#c62828';
        alerta.style.display = 'block';
        setTimeout(() => { alerta.style.display = 'none'; }, 2000);
    }
}

// Inicialização extra para garantir carregamento das opções de funcionamento caso necessário
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('funcionamento')) {
        preencherProfissionaisFuncionamento();
        listarFuncionamentos();
    }
});

// ---- SERVIÇOS ----

let modoEdicaoServico = null; // Guarda o ID do serviço que está sendo editado (se for o caso)

// Listar serviços (com filtro por nome)
async function listarServicos() {
    const nome = document.getElementById('filtroNomeServico')?.value?.trim() ?? '';
    let url = `${API_URL}/servicos`;
    if (nome) url += `?nome=${encodeURIComponent(nome)}`;
    const resp = await fetch(url);
    const dados = await resp.json();
    const tbody = document.getElementById('tbody-servicos');
    if (!tbody) return;
    tbody.innerHTML = '';
    // Monta as linhas da tabela de serviços
    dados.forEach(item => {
        tbody.innerHTML += `
            <tr>
                <td>${item.idServico}</td>
                <td>${item.nome}</td>
                <td>R$ ${Number(item.valor).toFixed(2)}</td>
                <td>
                    <button class="btn btn-editar" onclick="editarServico(${item.idServico}, '${item.nome}', ${item.valor})">Editar</button>
                    <button class="btn btn-rejeitar" onclick="removerServico(${item.idServico})">Remover</button>
                </td>
            </tr>
        `;
    });
}
window.listarServicos = listarServicos;

// Cadastrar ou editar um serviço
const formServico = document.getElementById('form-servico');
if (formServico) {
    formServico.onsubmit = async function(e) {
        e.preventDefault();
        const nome = document.getElementById('nome-servico').value.trim();
        const valor = parseFloat(document.getElementById('valor-servico').value);
        if (!nome || isNaN(valor)) return; // Validação simples

        let url = `${API_URL}/servicos`;
        let method = 'POST';
        let okMsg = 'Serviço cadastrado!';
        let body = { nome, valor };

        // Se estiver editando um serviço, muda para PUT
        if (modoEdicaoServico) {
            url += `/${modoEdicaoServico}`;
            method = 'PUT';
            okMsg = 'Serviço atualizado!';
        }

        try {
            const resp = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            if (resp.ok) {
                listarServicos();
                formServico.reset();
                modoEdicaoServico = null;
                document.getElementById('btn-cancelar-edicao-servico').style.display = 'none';
                exibirMensagem(okMsg, true);
            } else {
                exibirMensagem('Erro ao salvar serviço!', false);
            }
        } catch {
            exibirMensagem('Erro de conexão!', false);
        }
    }
}

// Preenche o formulário com os dados do serviço que está sendo editado
window.editarServico = function(id, nome, valor) {
    document.getElementById('nome-servico').value = nome;
    document.getElementById('valor-servico').value = valor;
    modoEdicaoServico = id;
    document.getElementById('btn-cancelar-edicao-servico').style.display = 'inline-block';
};

// Cancela o modo edição do serviço e limpa o formulário
document.getElementById('btn-cancelar-edicao-servico').onclick = function() {
    modoEdicaoServico = null;
    formServico.reset();
    this.style.display = 'none';
};

// Remove um serviço pelo ID
window.removerServico = async function(id) {
    if (!confirm("Deseja remover este serviço?")) return;
    try {
        const resp = await fetch(`${API_URL}/servicos/${id}`, { method: "DELETE" });
        if (resp.ok) {
            listarServicos();
            exibirMensagem('Serviço removido!', true);
        } else {
            exibirMensagem('Erro ao remover serviço!', false);
        }
    } catch {
        exibirMensagem('Erro de conexão!', false);
    }
};

// Botão para filtrar serviços na lista
document.getElementById('btnFiltrarServicos').onclick = function() {
    listarServicos();
};

// Garante que sempre que a aba de serviços for aberta, a lista será atualizada
document.querySelector('[onclick*="openTab(\'servicos\')"]')?.addEventListener('click', listarServicos);


// --- EDIÇÃO DE PROFISSIONAL ---

// Abre o modal de edição de profissional e preenche os campos com os dados do profissional selecionado
window.abrirModalEditarProf = async function(idProfissional) {
    const resp = await fetch(`${API_URL}/profissional/${idProfissional}`);
    if (!resp.ok) return alert("Erro ao buscar profissional.");
    const p = await resp.json();

    document.getElementById('edit-idProfissional').value = p.idProfissional;
    document.getElementById('edit-nomeProf').value = p.pessoa?.nome ?? '';
    document.getElementById('edit-emailProf').value = p.pessoa?.email ?? '';
    document.getElementById('edit-telefoneProf').value = p.telefone ?? '';
    document.getElementById('edit-nascProf').value = p.dataNascimento ?? '';

    document.getElementById('modal-editar-prof').style.display = 'flex';
}

// Fecha o modal de edição de profissional
window.fecharModalEditarProf = function() {
    document.getElementById('modal-editar-prof').style.display = 'none';
    document.getElementById('form-editar-prof').reset();
}

// Envia os dados do formulário de edição do profissional para a API
document.getElementById('form-editar-prof').onsubmit = async function(e) {
    e.preventDefault();
    const id = document.getElementById('edit-idProfissional').value;
    const nome = document.getElementById('edit-nomeProf').value.trim();
    const email = document.getElementById('edit-emailProf').value.trim();
    const telefone = document.getElementById('edit-telefoneProf').value.trim();
    const dataNascimento = document.getElementById('edit-nascProf').value;

    // Busca o profissional para pegar toda a estrutura (evita sobrescrever campos vazios)
    const resp = await fetch(`${API_URL}/profissional/${id}`);
    if (!resp.ok) return alert("Erro ao buscar profissional.");

    let prof = await resp.json();
    prof.pessoa = prof.pessoa || {};
    prof.pessoa.nome = nome;
    prof.pessoa.email = email;
    prof.telefone = telefone;
    prof.dataNascimento = dataNascimento;

    // Atualiza via PUT
    const resp2 = await fetch(`${API_URL}/profissional/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(prof)
    });

    if (resp2.ok) {
        exibirMensagem("Profissional atualizado!", true);
        fecharModalEditarProf();
        listarProfissionais();
    } else {
        exibirMensagem("Erro ao atualizar profissional!", false);
    }
};


// -- LISTA E BOTÕES DE CLIENTES --

// Lista clientes com filtros por nome, e-mail e telefone
async function listarClientes() {
    const nome = document.getElementById('filtroNomeCliente')?.value?.trim() ?? '';
    const email = document.getElementById('filtroEmailCliente')?.value?.trim() ?? '';
    const telefone = document.getElementById('filtroTelefoneCliente')?.value?.trim() ?? '';
    let url = `${API_URL}/clientes`;
    const params = [];
    if (nome) params.push(`nome=${encodeURIComponent(nome)}`);
    if (email) params.push(`email=${encodeURIComponent(email)}`);
    if (telefone) params.push(`telefone=${encodeURIComponent(telefone)}`);
    if (params.length) url += `?${params.join('&')}`;

    const resp = await fetch(url);
    const dados = await resp.json();
    const tbody = document.getElementById('tbody-clientes');
    if (!tbody) return;
    tbody.innerHTML = '';
    // Monta a tabela de clientes
    dados.forEach(item => {
        tbody.innerHTML += `
            <tr>
                <td>${item.idCliente}</td>
                <td>${item.pessoa?.nome ?? ''}</td>
                <td>${item.pessoa?.email ?? ''}</td>
                <td>${item.telefone ?? ''}</td>
                <td>${item.dataNascimento ?? ''}</td>
                <td>
                    <button class="btn btn-editar" onclick="abrirModalEditarCliente(${item.idCliente})">Editar</button>
                    <button class="btn btn-rejeitar" onclick="removerCliente(${item.idCliente})">Remover</button>
                </td>
            </tr>
        `;
    });
}
window.listarClientes = listarClientes;

// Botão para filtrar clientes
document.getElementById('btnFiltrarClientes').onclick = listarClientes;

// --- MODAL EDIÇÃO CLIENTE ---

// Abre o modal de edição e preenche os campos com os dados do cliente
window.abrirModalEditarCliente = async function(idCliente) {
    const resp = await fetch(`${API_URL}/clientes/${idCliente}`);
    if (!resp.ok) return alert("Erro ao buscar cliente.");
    const c = await resp.json();

    document.getElementById('edit-idCliente').value = c.idCliente;
    document.getElementById('edit-nomeCliente').value = c.pessoa?.nome ?? '';
    document.getElementById('edit-emailCliente').value = c.pessoa?.email ?? '';
    document.getElementById('edit-telefoneCliente').value = c.telefone ?? '';
    document.getElementById('edit-nascCliente').value = c.dataNascimento ?? '';

    document.getElementById('modal-editar-cliente').style.display = 'flex';
}

// Fecha o modal de edição de cliente
window.fecharModalEditarCliente = function() {
    document.getElementById('modal-editar-cliente').style.display = 'none';
    document.getElementById('form-editar-cliente').reset();
}

// Envia o formulário de edição de cliente para a API
document.getElementById('form-editar-cliente').onsubmit = async function(e) {
    e.preventDefault();
    const id = document.getElementById('edit-idCliente').value;
    const nome = document.getElementById('edit-nomeCliente').value.trim();
    const email = document.getElementById('edit-emailCliente').value.trim();
    const telefone = document.getElementById('edit-telefoneCliente').value.trim();
    const dataNascimento = document.getElementById('edit-nascCliente').value;

    // Busca toda a estrutura do cliente (para não perder campos que não estão no form)
    const resp = await fetch(`${API_URL}/clientes/${id}`);
    if (!resp.ok) return alert("Erro ao buscar cliente.");
    let cliente = await resp.json();
    cliente.pessoa = cliente.pessoa || {};
    cliente.pessoa.nome = nome;
    cliente.pessoa.email = email;
    cliente.telefone = telefone;
    cliente.dataNascimento = dataNascimento;

    const resp2 = await fetch(`${API_URL}/clientes/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cliente)
    });

    if (resp2.ok) {
        exibirMensagem("Cliente atualizado!", true);
        fecharModalEditarCliente();
        listarClientes();
    } else {
        exibirMensagem("Erro ao atualizar cliente!", false);
    }
};

// Remove cliente pelo ID, com confirmação
window.removerCliente = async function(id) {
    if (!confirm("Deseja remover este cliente?")) return;
    try {
        const resp = await fetch(`${API_URL}/clientes/${id}`, { method: "DELETE" });
        if (resp.ok) {
            listarClientes();
            exibirMensagem('Cliente removido!', true);
        } else {
            exibirMensagem('Erro ao remover cliente!', false);
        }
    } catch {
        exibirMensagem('Erro de conexão!', false);
    }
}
