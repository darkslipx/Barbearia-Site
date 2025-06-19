const API_URL = "http://localhost:8080";
const CLIENTE_ID = localStorage.getItem('clienteId') || 1;

const tbody = document.getElementById('tbody-meus-agendamentos');
const msgDiv = document.getElementById('msg-agendamentos');

async function listarMeusAgendamentos() {
    tbody.innerHTML = '<tr><td colspan="5">Carregando...</td></tr>';
    try {
        // Busca todos os agendamentos do cliente autenticado
        const resp = await fetch(`${API_URL}/agendamentos?clienteId=${CLIENTE_ID}`);
        if (!resp.ok) throw new Error("Erro ao buscar agendamentos.");
        const agendamentos = await resp.json();
        if (!agendamentos.length) {
            tbody.innerHTML = '<tr><td colspan="5">Nenhum agendamento encontrado.</td></tr>';
            return;
        }
        tbody.innerHTML = '';
        agendamentos.forEach(item => {
            tbody.innerHTML += `
                <tr>
                    <td>${item.data || ""}</td>
                    <td>${item.horario?.horaInicio ? item.horario.horaInicio.substring(0,5) : ""}</td>
                    <td>${item.profissional?.pessoa?.nome || ""}</td>
                    <td>${item.servico?.nome || item.servico || ""}</td>
                    <td class="status-${(item.status ?? '').toLowerCase()}">${item.status || ""}</td>
                </tr>
            `;
        });
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="5">Erro ao carregar agendamentos.</td></tr>';
        msgDiv.textContent = "Erro ao carregar agendamentos.";
    }
}

document.addEventListener('DOMContentLoaded', listarMeusAgendamentos);
