function mostrarDetalhes(botao) {
    // Pega os dados da linha (tr) do botão clicado
    var tr = botao.closest('tr');
    var tds = tr.querySelectorAll('td');
    var nome = tds[0].textContent;
    var dias = tds[1].textContent;
    var periodo = tds[2].textContent;
    var inicio = tds[3].textContent;
    var fim = tds[4].textContent;

    var html = `
        <strong>Profissional:</strong> ${nome} <br>
        <strong>Dias:</strong> ${dias} <br>
        <strong>Período:</strong> ${periodo} <br>
        <strong>Início:</strong> ${inicio} <br>
        <strong>Fim:</strong> ${fim}
    `;

    document.getElementById('detalhes-content').innerHTML = html;
    document.getElementById('modal-detalhes').style.display = 'flex';
}

function fecharModalDetalhes() {
    document.getElementById('modal-detalhes').style.display = 'none';
}
