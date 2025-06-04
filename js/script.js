function openTab(tabId) {
    // Oculta todos os conteúdos das abas
    const contents = document.querySelectorAll('.tab-content');
    contents.forEach(content => content.classList.remove('active'));

    // Remove a classe 'active' de todos os botões de aba
    const buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(button => button.classList.remove('active'));

    // Exibe o conteúdo da aba selecionada
    const selectedContent = document.getElementById(tabId);
    if (selectedContent) {
        selectedContent.classList.add('active');
    }

    // Adiciona a classe 'active' ao botão correspondente
    const selectedButton = Array.from(buttons).find(button => button.textContent.trim().toLowerCase() === tabId);
    if (selectedButton) {
        selectedButton.classList.add('active');
    }
}
