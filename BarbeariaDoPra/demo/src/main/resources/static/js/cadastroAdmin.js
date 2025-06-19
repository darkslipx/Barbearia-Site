const API_URL = 'http://localhost:8080';

document.getElementById('form-cadastro-admin').addEventListener('submit', async function (e) {
    e.preventDefault();

    const nome = document.getElementById('primeiro-nome').value.trim();
    const sobrenome = document.getElementById('ultimo-nome').value.trim();
    const email = document.getElementById('email').value.trim();
    const telefone = document.getElementById('telefone').value.trim();
    const dataNascimento = document.getElementById('data-nascimento').value;
    const senha = document.getElementById('password').value;
    const confirmarSenha = document.getElementById('confirmar-password').value;
    const msgDiv = document.getElementById('msg-cadastro-admin');

    if (senha !== confirmarSenha) {
        msgDiv.innerHTML = `<span style="color:#c62828;">As senhas não coincidem!</span>`;
        return;
    }

    const profissional = {
    pessoa: {
        nome: nome + ' ' + sobrenome,
        email: email,
        senha: senha,
        // nivel: 'PROFISSIONAL', // Se quiser pode enviar ou deixa o backend definir
    },
    telefone: telefone,
    dataNascimento: dataNascimento
};


    try {
            const resp = await fetch(`${API_URL}/profissional`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(profissional)
        });

        if (resp.ok) {
            msgDiv.innerHTML = `<span style="color:green;">Cadastro realizado com sucesso!</span>`;
            document.getElementById('form-cadastro-admin').reset();
        } else {
            const error = await resp.text();
            msgDiv.innerHTML = `<span style="color:#c62828;">Erro ao cadastrar: ${error}</span>`;
        }
    } catch (err) {
        msgDiv.innerHTML = `<span style="color:#c62828;">Falha de conexão com o servidor!</span>`;
    }
});
