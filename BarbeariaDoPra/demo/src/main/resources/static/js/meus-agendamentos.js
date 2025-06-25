const API_URL = "http://localhost:8080";
const CLIENTE_ID = localStorage.getItem('clienteId') || 1; // ID do cliente logado, padrão 1 para testes

const tbody = document.getElementById('tbody-meus-agendamentos'); // Corpo da tabela onde os agendamentos serão listados
const msgDiv = document.getElementById('msg-agendamentos');      // Div para mensagens ao usuário

// Função para listar agendamentos do cliente autenticado
async function listarMeusAgendamentos() {
    tbody.innerHTML = '<tr><td colspan="5">Carregando...</td></tr>'; // Mostra mensagem de carregamento
    try {
        // Busca todos os agendamentos do cliente autenticado
        const resp = await fetch(`${API_URL}/agendamentos?clienteId=${CLIENTE_ID}`);
        if (!resp.ok) throw new Error("Erro ao buscar agendamentos.");
        const agendamentos = await resp.json();

        // Caso não haja agendamentos, mostra mensagem
        if (!agendamentos.length) {
            tbody.innerHTML = '<tr><td colspan="5">Nenhum agendamento encontrado.</td></tr>';
            return;
        }

        // Limpa o tbody e preenche cada linha com os dados dos agendamentos
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
        // Em caso de erro na requisição, mostra mensagem de erro na tabela e na div de mensagem
        tbody.innerHTML = '<tr><td colspan="5">Erro ao carregar agendamentos.</td></tr>';
        msgDiv.textContent = "Erro ao carregar agendamentos.";
    }
}

// Ao carregar a página, já lista os agendamentos
document.addEventListener('DOMContentLoaded', listarMeusAgendamentos);
