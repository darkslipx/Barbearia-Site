const API_URL = "http://localhost:8080";

// Seletores de elementos da página
const selectProf = document.getElementById('profissional');
const selectDia = document.getElementById('dia');
const inputHoraInicio = document.getElementById('horaInicio');
const inputHoraFim = document.getElementById('horaFim');
const formHorario = document.getElementById('form-horario');
const tbodyHorarios = document.getElementById('horarios-tbody');

// Dicionários para tradução entre Label e Enum do backend
const diasEnum = {
    "Segunda-feira": "SEGUNDA_FEIRA",
    "Terça-feira": "TERCA_FEIRA",
    "Quarta-feira": "QUARTA_FEIRA",
    "Quinta-feira": "QUINTA_FEIRA",
    "Sexta-feira": "SEXTA_FEIRA",
    "Sábado": "SABADO",
    "Domingo": "DOMINGO"
};
const diasLabel = {
    "SEGUNDA_FEIRA": "Segunda-feira",
    "TERCA_FEIRA": "Terça-feira",
    "QUARTA_FEIRA": "Quarta-feira",
    "QUINTA_FEIRA": "Quinta-feira",
    "SEXTA_FEIRA": "Sexta-feira",
    "SABADO": "Sábado",
    "DOMINGO": "Domingo"
};

// Carrega a lista de profissionais para o select
async function carregarProfissionais() {
    const resp = await fetch(`${API_URL}/profissional`);
    const profs = await resp.json();
    selectProf.innerHTML = '<option value="">Selecione</option>';
    profs.forEach(p => {
        selectProf.innerHTML += `<option value="${p.idProfissional}">${p.pessoa?.nome ?? p.nome}</option>`;
    });
}
carregarProfissionais(); // Chama ao iniciar

// Carrega a tabela de horários cadastrados
async function carregarHorarios() {
    tbodyHorarios.innerHTML = '<tr><td colspan="5">Carregando...</td></tr>';
    try {
        const resp = await fetch(`${API_URL}/horario`);
        if (!resp.ok) throw new Error("Falha no fetch");
        const horarios = await resp.json();
        if (!horarios.length) {
            tbodyHorarios.innerHTML = '<tr><td colspan="5">Nenhum horário cadastrado.</td></tr>';
            return;
        }
        tbodyHorarios.innerHTML = "";
        horarios.forEach(h => {
            tbodyHorarios.innerHTML += `
                <tr>
                    <td>${h.profissional?.pessoa?.nome ?? ''}</td>
                    <td>${diasLabel[h.diaSemana] ?? h.diaSemana}</td>
                    <td>${h.horaInicio?.substring(0,5)}</td>
                    <td>${h.horaFim?.substring(0,5)}</td>
                    <td>
                        <button class="btn btn-rejeitar" onclick="abrirModalExcluir(${h.idHorario})">Excluir</button>
                    </td>
                </tr>
            `;
        });
    } catch (e) {
        tbodyHorarios.innerHTML = '<tr><td colspan="5">Erro ao carregar horários.</td></tr>';
        console.error(e);
    }
}
window.carregarHorarios = carregarHorarios; // Disponibiliza no escopo global para reuso em outras funções
carregarHorarios(); // Chama ao iniciar

// SUBMISSÃO DO FORMULÁRIO DE NOVO HORÁRIO
formHorario.addEventListener('submit', async function(e) {
    e.preventDefault();
    const profissionalId = selectProf.value;
    const diaLabel = selectDia.value;
    const horaInicio = inputHoraInicio.value;
    const horaFim = inputHoraFim.value;

    // Validação simples
    if (!profissionalId || !diaLabel || !horaInicio || !horaFim) {
        alert('Preencha todos os campos!');
        return;
    }
    const diaSemana = diasEnum[diaLabel]; // Converte para Enum

    try {
        const resp = await fetch(`${API_URL}/horario`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                profissional: { idProfissional: Number(profissionalId) },
                diaSemana: diaSemana,
                horaInicio: horaInicio,
                horaFim: horaFim,
                bloqueado: false // Por padrão, cadastra desbloqueado
            })
        });

        if (resp.ok) {
            carregarHorarios(); // Atualiza tabela após cadastro
            formHorario.reset(); // Limpa o form
        } else {
            alert('Erro ao salvar horário');
        }
    } catch {
        alert('Erro de conexão');
    }
});
