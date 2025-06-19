const API_URL = "http://localhost:8080";
const CLIENTE_ID = localStorage.getItem('clienteId') || 1;

const selectProf = document.getElementById('profissional');
const dateInput = document.getElementById('date');
const selectHorario = document.getElementById('horario');
const msgDiv = document.getElementById('msg-agendamento');

// Ao carregar a página, exibe o nome salvo no localStorage
document.addEventListener('DOMContentLoaded', function() {
    const nome = localStorage.getItem('usuarioNome') || 'Usuário';
    document.getElementById('nome-usuario-header').textContent = nome;
});


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
document.addEventListener('DOMContentLoaded', carregarProfissionais);

// Ao selecionar profissional, libera calendário e limita dias válidos
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

        // Habilita o campo ANTES de criar o Flatpickr
        dateInput.disabled = false;

        // Destroi o flatpickr antigo se existir
        if (dateInput._flatpickr) {
            dateInput._flatpickr.destroy();
        }

        // Inicializa o Flatpickr
        flatpickr("#date", {
            locale: "pt",
            dateFormat: "Y-m-d",
            minDate: "today",
            onChange: function(selectedDates, dateStr, instance) {
                if (!dateStr) return;
                const data = new Date(dateStr + "T00:00:00");
                const dayEnum = [
                    "DOMINGO", "SEGUNDA_FEIRA", "TERCA_FEIRA", "QUARTA_FEIRA", "QUINTA_FEIRA", "SEXTA_FEIRA", "SABADO"
                ][data.getDay()];
                if (!diasDisponiveisEnum.includes(dayEnum)) {
                    instance.clear();
                    msgDiv.innerHTML = `<span style="color:#c62828;">O profissional não atende nesse dia da semana.</span>`;
                    selectHorario.innerHTML = '<option value="">Selecione um horário</option>';
                    selectHorario.disabled = true;
                } else {
                    msgDiv.innerHTML = "";
                    selectHorario.innerHTML = '<option value="">Buscando horários...</option>';
                    selectHorario.disabled = false;
                    carregarHorariosDisponiveis();
                }
            }
        });

        // Limpa o valor antigo
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
    const profissionalId = selectProf.value;
    const horario = selectHorario.value;
    const servicoId = Number(document.getElementById('servico').value);
    const data = dateInput.value;
    if (!profissionalId || !horario || !data) {
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
                horario: { idHorario: Number(horario) }, // Corrigido para enviar o ID
                status: 'PENDENTE',
                data: data,
                servico: servico
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
    const clienteId = localStorage.getItem('usuarioId') || CLIENTE_ID;
    const tbody = document.getElementById('tbody-agendamentos');
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

            // Só exibe botão se for PENDENTE ou CONFIRMADO
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

// Carrega ao abrir a página:
document.addEventListener('DOMContentLoaded', carregarAgendamentosCliente);

