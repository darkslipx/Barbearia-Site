const API_URL = "http://localhost:8080";
const CLIENTE_ID = localStorage.getItem('clienteId');

const selectProf = document.getElementById('profissional');
const dateInput = document.getElementById('date');
const selectHorario = document.getElementById('horario');
const msgDiv = document.getElementById('msg-agendamento');
const selectServico = document.getElementById('servico');

// Ao carregar a página, exibe o nome salvo no localStorage
document.addEventListener('DOMContentLoaded', function() {
    const nome = localStorage.getItem('usuarioNome') || 'Usuário';
    document.getElementById('nome-usuario-header').textContent = nome;
});


// --------- Flatpickr Instance ---------
let flatpickrInstance = null;
let diasDisponiveis = []; // Salva os dias válidos recebidos do backend

function diaEnumParaNumero(enumStr) {
    // Domingo=0, Segunda=1, ... Sábado=6
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

function flatpickrEnableDates(date) {
    // Só permite selecionar dias presentes em diasDisponiveis
    if (!diasDisponiveis.length) return false;
    return diasDisponiveis.includes(date.getDay());
}

// Inicializa o Flatpickr
function inicializarFlatpickr() {
    if (flatpickrInstance) {
        flatpickrInstance.destroy();
    }
    flatpickrInstance = flatpickr(dateInput, {
        locale: "pt",
        dateFormat: "Y-m-d",
        minDate: "today",
        disableMobile: true,
        enable: [flatpickrEnableDates],
        onChange: function(selectedDates, dateStr) {
            if (!dateStr) return;
            msgDiv.innerHTML = "";
            selectHorario.innerHTML = '<option value="">Buscando horários...</option>';
            selectHorario.disabled = false;
            carregarHorariosDisponiveis();
        }
    });
}

// Carrega profissionais ao abrir a página
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

// Carrega serviços ao abrir a página
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

// Quando seleciona um profissional, atualiza dias e Flatpickr
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

// Limita datas do calendário conforme dias disponíveis do Funcionamento
async function limitarDiasDisponiveisCalendario() {
    const profId = selectProf.value;
    if (!profId) return;
    try {
        const resp = await fetch(`${API_URL}/funcionamento/profissional/${profId}/dias-disponiveis`);
        if (!resp.ok) throw new Error();
        const diasDisponiveisEnum = await resp.json();
        // Converte para números dos dias da semana
        diasDisponiveis = diasDisponiveisEnum.map(diaEnumParaNumero);

        inicializarFlatpickr();

        // Limpa e desabilita o horário até que uma data válida seja escolhida
        dateInput.value = "";
        selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
        selectHorario.disabled = true;
    } catch {
        msgDiv.innerHTML = '<span style="color:#c62828;">Erro ao validar dias disponíveis!</span>';
    }
}

// Carrega horários disponíveis para o dia/profissional
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

// Submete o agendamento
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
                servico: { idServico: Number(servico) } // <- CORRETO!
            })
        });
        if (resp.ok) {
            msgDiv.innerHTML = `<span style="color:green;">Agendamento realizado com sucesso!</span>`;
            this.reset();
            dateInput.disabled = true;
            selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
            selectHorario.disabled = true;
            carregarAgendamentosCliente(); // Atualiza a lista!
        } else {
            msgDiv.innerHTML = `<span style="color:#c62828;">Erro ao agendar!</span>`;
        }
    } catch (err) {
        msgDiv.innerHTML = `<span style="color:#c62828;">Erro de conexão!</span>`;
    }
};


// Carrega agendamentos do cliente, agora com botão Cancelar
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
        //ordenar do mais recente para o mais antigo
        agendamentos.sort((a, b) => {
            if (a.data < b.data) return 1;
            if (a.data > b.data) return -1;
            // Se as datas forem iguais, ordena pelo horário
            if ((a.horario?.horaInicio || "") < (b.horario?.horaInicio || "")) return 1;
            if ((a.horario?.horaInicio || "") > (b.horario?.horaInicio || "")) return -1;
            return 0;
        });
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
        // Evento de cancelar
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

// Carrega tudo ao abrir a página:
document.addEventListener('DOMContentLoaded', () => {
    carregarProfissionais();
    carregarServicos();
    carregarAgendamentosCliente();
    inicializarFlatpickr();
});
