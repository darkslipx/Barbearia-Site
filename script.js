function openTab(tabId) {
    // Esconde todos os conteúdos das abas
    var contents = document.querySelectorAll('.tab-content');
    contents.forEach(function(content) {
        content.classList.remove('active');
    });

    // Remove 'active' de todos os botões
    var buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(function(button) {
        button.classList.remove('active');
    });

    // Mostra a aba clicada
    var tabContent = document.getElementById(tabId);
    if (tabContent) {
        tabContent.classList.add('active');
    }

    // Ativa o botão correto
    // (seleciona o botão que chama openTab com o id correspondente)
    buttons.forEach(function(button) {
        if (button.getAttribute('onclick') && button.getAttribute('onclick').includes(tabId)) {
            button.classList.add('active');
        }
    });
}
