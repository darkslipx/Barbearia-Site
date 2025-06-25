const API_URL = "http://localhost:8080";
const CLIENTE_ID = localStorage.getItem('clienteId'); // Pega o ID do cliente logado do localStorage

// Elementos principais do DOM usados no formulário de agendamento
const selectProf = document.getElementById('profissional');
const dateInput = document.getElementById('date');
const selectHorario = document.getElementById('horario');
const msgDiv = document.getElementById('msg-agendamento');
const selectServico = document.getElementById('servico');

// Exibe o nome do usuário no topo, caso exista no localStorage
document.addEventListener('DOMContentLoaded', function() {
    const nome = localStorage.getItem('usuarioNome') || 'Usuário';
    document.getElementById('nome-usuario-header').textContent = nome;
});

// --- Controle do calendário (Flatpickr) ---
let flatpickrInstance = null;
let diasDisponiveis = []; // Array de dias da semana disponíveis, vindos do backend

// Converte string enum do backend em número do dia da semana (0=Domingo, ..., 6=Sábado)
function diaEnumParaNumero(enumStr) {
    return {
        "DOMINGO": 0,
        "SEGUNDA_FEIRA": 1,
        "TERCA_FEIRA": 2,
        "QUARTA_FEIRA": 3,
        "QUINTA_FEIRA": 4,
        "SEXTA_FEIRA": 5,
        "SABADO": 6
    }[enumStr];
}

// Função passada ao flatpickr para habilitar apenas dias válidos no calendário
function flatpickrEnableDates(date) {
    if (!diasDisponiveis.length) return false;
    return diasDisponiveis.includes(date.getDay());
}

// Inicializa o calendário Flatpickr com dias habilitados conforme funcionamento do profissional
function inicializarFlatpickr() {
    if (flatpickrInstance) {
        flatpickrInstance.destroy(); // Evita instâncias duplicadas
    }
    flatpickrInstance = flatpickr(dateInput, {
        locale: "pt",
        dateFormat: "Y-m-d",
        minDate: "today",
        disableMobile: true,
        enable: [flatpickrEnableDates], // Habilita só os dias permitidos
        onChange: function(selectedDates, dateStr) {
            if (!dateStr) return;
            msgDiv.innerHTML = "";
            selectHorario.innerHTML = '<option value="">Buscando horários...</option>';
            selectHorario.disabled = false;
            carregarHorariosDisponiveis();
        }
    });
}

// Carrega profissionais ao abrir a tela e monta o select
async function carregarProfissionais() {
    selectProf.innerHTML = '<option value="">Carregando...</option>';
    try {
        const resp = await fetch(`${API_URL}/profissional`);
        if (!resp.ok) throw new Error("Erro ao carregar profissionais.");
        const profs = await resp.json();
        selectProf.innerHTML = '<option value="">Selecione um profissional</option>';
        profs.forEach(p => {
            selectProf.innerHTML += `<option value="${p.idProfissional}">${p.pessoa?.nome ?? p.nome}</option>`;
        });
    } catch {
        selectProf.innerHTML = '<option value="">Erro ao carregar</option>';
    }
    selectProf.disabled = false;
    dateInput.value = "";
    dateInput.disabled = true;
    selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
    selectHorario.disabled = true;
}

// Carrega serviços e popula o select do formulário
async function carregarServicos() {
    selectServico.innerHTML = '<option value="">Carregando serviços...</option>';
    try {
        const resp = await fetch(`${API_URL}/servicos`);
        if (!resp.ok) throw new Error();
        const servicos = await resp.json();
        selectServico.innerHTML = '<option value="">Selecione um serviço</option>';
        servicos.forEach(s => {
            selectServico.innerHTML += `<option value="${s.idServico}">${s.nome} - R$ ${s.valor.toFixed(2)}</option>`;
        });
    } catch {
        selectServico.innerHTML = '<option value="">Erro ao carregar</option>';
    }
}

// Quando muda o profissional, reseta datas/horários e busca os dias disponíveis daquele profissional
selectProf.addEventListener('change', async function () {
    if (selectProf.value) {
        dateInput.disabled = false;
        dateInput.value = "";
        selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
        selectHorario.disabled = true;
        await limitarDiasDisponiveisCalendario();
    } else {
        dateInput.disabled = true;
        dateInput.value = "";
        selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
        selectHorario.disabled = true;
    }
});

// Limita os dias disponíveis do calendário conforme o funcionamento do profissional
async function limitarDiasDisponiveisCalendario() {
    const profId = selectProf.value;
    if (!profId) return;
    try {
        const resp = await fetch(`${API_URL}/funcionamento/profissional/${profId}/dias-disponiveis`);
        if (!resp.ok) throw new Error();
        const diasDisponiveisEnum = await resp.json();
        diasDisponiveis = diasDisponiveisEnum.map(diaEnumParaNumero); // Converte para [1, 3, 5]...
        inicializarFlatpickr();
        dateInput.value = "";
        selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
        selectHorario.disabled = true;
    } catch {
        msgDiv.innerHTML = '<span style="color:#c62828;">Erro ao validar dias disponíveis!</span>';
    }
}

// Carrega horários realmente disponíveis para o profissional e data escolhidos
async function carregarHorariosDisponiveis() {
    const profId = selectProf.value;
    const dataSelecionada = dateInput.value;
    selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
    selectHorario.disabled = true;
    if (!profId || !dataSelecionada) return;
    try {
        const resp = await fetch(`${API_URL}/agendamentos/horarios-disponiveis?profissionalId=${profId}&data=${dataSelecionada}`);
        if (!resp.ok) throw new Error();
        const horarios = await resp.json();
        if (!horarios.length) {
            selectHorario.innerHTML = '<option value="">Nenhum horário disponível</option>';
            selectHorario.disabled = true;
            return;
        }
        horarios.forEach(h => {
            selectHorario.innerHTML += `<option value="${h.idHorario}">${h.horaInicio.substring(0, 5)}</option>`;
        });
        selectHorario.disabled = false;
    } catch {
        selectHorario.innerHTML = '<option value="">Erro ao buscar horários</option>';
        selectHorario.disabled = true;
    }
}

// Envio do formulário de agendamento
document.getElementById('form-agendamento').onsubmit = async function(e) {
    e.preventDefault();
    if (!CLIENTE_ID) {
        msgDiv.innerHTML = `<span style="color:#c62828;">Erro: ID do cliente não encontrado. Faça login novamente!</span>`;
        return;
    }
    const profissionalId = selectProf.value;
    const horario = selectHorario.value;
    const servico = selectServico.value;
    const data = dateInput.value;
    if (!profissionalId || !horario || !data || !servico) {
        msgDiv.innerHTML = `<span style="color:#c62828;">Preencha todos os campos!</span>`;
        return;
    }
    try {
        const resp = await fetch(`${API_URL}/agendamentos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cliente: { idCliente: CLIENTE_ID },
                profissional: { idProfissional: Number(profissionalId) },
                horario: { idHorario: Number(horario) },
                status: 'PENDENTE',
                data: data,
                servico: { idServico: Number(servico) }
            })
        });
        if (resp.ok) {
            msgDiv.innerHTML = `<span style="color:green;">Agendamento realizado com sucesso!</span>`;
            this.reset();
            dateInput.disabled = true;
            selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
            selectHorario.disabled = true;
            carregarAgendamentosCliente(); // Atualiza a lista de agendamentos do cliente
        } else {
            msgDiv.innerHTML = `<span style="color:#c62828;">Erro ao agendar!</span>`;
        }
    } catch (err) {
        msgDiv.innerHTML = `<span style="color:#c62828;">Erro de conexão!</span>`;
    }
};

// Carrega agendamentos do cliente logado, mostrando botão cancelar se status permitir
async function carregarAgendamentosCliente() {
    const clienteId = localStorage.getItem('clienteId');
    const tbody = document.getElementById('tbody-agendamentos');
    if (!clienteId) {
        tbody.innerHTML = '<tr><td colspan="6">Erro: ID do cliente não encontrado. Faça login novamente!</td></tr>';
        return;
    }
    tbody.innerHTML = '<tr><td colspan="6">Carregando...</td></tr>';
    try {
        const resp = await fetch(`${API_URL}/agendamentos/cliente/${clienteId}`);
        if (!resp.ok) throw new Error();
        const agendamentos = await resp.json();
        if (!agendamentos.length) {
            tbody.innerHTML = '<tr><td colspan="6">Nenhum agendamento encontrado.</td></tr>';
            return;
        }
        tbody.innerHTML = "";
        agendamentos.forEach(ag => {
            let statusClass = "status-pendente";
            if (ag.status === "RECUSADO") statusClass = "status-recusado";
            else if (ag.status === "CONFIRMADO") statusClass = "status-confirmado";
            else if (ag.status === "CANCELADO") statusClass = "status-cancelado";
            let botaoCancelar = "";
            // Permite cancelar apenas se pendente ou confirmado
            if (["PENDENTE", "CONFIRMADO"].includes(ag.status)) {
                botaoCancelar = `<button class="btn-cancelar" data-id="${ag.idAgendamento}">Cancelar</button>`;
            }
            tbody.innerHTML += `
                <tr>
                    <td>${ag.data || ""}</td>
                    <td>${ag.horario?.horaInicio?.substring(0,5) || ""}</td>
                    <td>${ag.profissional?.pessoa?.nome || ag.profissional?.nome || ""}</td>
                    <td>${ag.servico?.nome || ag.servico || ""}</td>
                    <td><span class="${statusClass}">${ag.status}</span></td>
                    <td>${botaoCancelar}</td>
                </tr>
            `;
        });
        // Evento para botão de cancelar agendamento
        tbody.querySelectorAll('.btn-cancelar').forEach(btn => {
            btn.onclick = async function() {
                const idAgendamento = this.getAttribute('data-id');
                if (confirm("Tem certeza que deseja cancelar este agendamento?")) {
                    try {
                        const resp = await fetch(`${API_URL}/agendamentos/${idAgendamento}/cancelar`, {
                            method: "PUT"
                        });
                        if (resp.ok) {
                            carregarAgendamentosCliente();
                        } else {
                            alert("Não foi possível cancelar.");
                        }
                    } catch {
                        alert("Erro ao cancelar agendamento.");
                    }
                }
            }
        });
    } catch {
        tbody.innerHTML = '<tr><td colspan="6">Erro ao carregar agendamentos.</td></tr>';
    }
}

// Carrega tudo ao abrir a tela: profissionais, serviços, agendamentos e calendário
document.addEventListener('DOMContentLoaded', () => {
    carregarProfissionais();
    carregarServicos();
    carregarAgendamentosCliente();
    inicializarFlatpickr();
});
