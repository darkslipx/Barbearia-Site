// Função para mostrar o modal de detalhes ao clicar no botão
function mostrarDetalhes(botao) {
    // Pega a linha (tr) do botão clicado
    var tr = botao.closest('tr');
    // Seleciona todas as células (tds) dessa linha
    var tds = tr.querySelectorAll('td');
    // Extrai os dados de cada coluna
    var nome = tds[0].textContent;
    var dias = tds[1].textContent;
    var periodo = tds[2].textContent;
    var inicio = tds[3].textContent;
    var fim = tds[4].textContent;

    // Monta o HTML com os detalhes formatados
    var html = `
        <strong>Profissional:</strong> ${nome} <br>
        <strong>Dias:</strong> ${dias} <br>
        <strong>Período:</strong> ${periodo} <br>
        <strong>Início:</strong> ${inicio} <br>
        <strong>Fim:</strong> ${fim}
    `;

    // Insere o HTML no conteúdo do modal
    document.getElementById('detalhes-content').innerHTML = html;
    // Exibe o modal
    document.getElementById('modal-detalhes').style.display = 'flex';
}

// Função para fechar o modal de detalhes
function fecharModalDetalhes() {
    document.getElementById('modal-detalhes').style.display = 'none';
}
