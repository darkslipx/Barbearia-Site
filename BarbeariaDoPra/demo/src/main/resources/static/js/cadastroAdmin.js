const API_URL = 'http://localhost:8080';

// Adiciona o listener de submit ao formulário de cadastro de admin/profissional
document.getElementById('form-cadastro-admin').addEventListener('submit', async function (e) {
    e.preventDefault(); // Evita que o form recarregue a página

    // Captura os valores dos campos do formulário
    const nome = document.getElementById('primeiro-nome').value.trim();
    const sobrenome = document.getElementById('ultimo-nome').value.trim();
    const email = document.getElementById('email').value.trim();
    const telefone = document.getElementById('telefone').value.trim();
    const dataNascimento = document.getElementById('data-nascimento').value;
    const senha = document.getElementById('password').value;
    const confirmarSenha = document.getElementById('confirmar-password').value;
    const msgDiv = document.getElementById('msg-cadastro-admin'); // Div para mostrar mensagens ao usuário

    // Validação: Confirma se as senhas coincidem
    if (senha !== confirmarSenha) {
        msgDiv.innerHTML = `<span style="color:#c62828;">As senhas não coincidem!</span>`;
        return;
    }

    // Monta o objeto profissional conforme esperado pelo backend
    const profissional = {
        pessoa: {
            nome: nome + ' ' + sobrenome, // Junta nome e sobrenome
            email: email,
            senha: senha,
            // nivel: 'PROFISSIONAL', // (opcional) O backend pode definir automaticamente
        },
        telefone: telefone,
        dataNascimento: dataNascimento
    };

    // Envia o cadastro via POST para o endpoint de profissionais
    try {
        const resp = await fetch(`${API_URL}/profissional`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(profissional)
        });

        // Se cadastro realizado com sucesso
        if (resp.ok) {
            msgDiv.innerHTML = `<span style="color:green;">Cadastro realizado com sucesso!</span>`;
            document.getElementById('form-cadastro-admin').reset();
        } else {
            // Se der erro, exibe mensagem vinda do backend
            const error = await resp.text();
            msgDiv.innerHTML = `<span style="color:#c62828;">Erro ao cadastrar: ${error}</span>`;
        }
    } catch (err) {
        // Falha de conexão com o servidor
        msgDiv.innerHTML = `<span style="color:#c62828;">Falha de conexão com o servidor!</span>`;
    }
});
