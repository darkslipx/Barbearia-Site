const API_URL = "http://localhost:8080";

// Cadastro de Cliente
const formCadastro = document.getElementById('form-cadastro');
if (formCadastro) {
    formCadastro.addEventListener('submit', async function (e) {
        e.preventDefault();
        const nome = document.getElementById('primeiro-nome').value.trim();
        const sobrenome = document.getElementById('ultimo-nome').value.trim();
        const email = document.getElementById('email').value.trim();
        const telefone = document.getElementById('telefone').value.trim();
        const dataNascimento = document.getElementById('data-nascimento').value;
        const senha = document.getElementById('password').value;
        const confirmarSenha = document.getElementById('confirmar-password').value;
        const msgDiv = document.getElementById('msg-cadastro') || document.createElement("div");
        if (senha !== confirmarSenha) {
            msgDiv.innerHTML = `<span style="color:#c62828;">As senhas não coincidem!</span>`;
            return;
        }
        const pessoa = {
            nome: nome + ' ' + sobrenome,
            email: email,
            nivel: "CLIENTE", // <- SETA FIXO
            senha: senha      // <- Inclua a senha aqui se ela pertence à PessoaEntity
            // Remova dataNascimento daqui se não faz parte da PessoaEntity
        };
        const cliente = {
            pessoa: pessoa,
            telefone: telefone,
            dataNascimento: dataNascimento // <- Se faz parte de ClienteEntity, mantenha aqui
        };
        try {
            const resp = await fetch(`${API_URL}/clientes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(cliente)
            });
            if (resp.ok) {
                msgDiv.innerHTML = `<span style="color:green;">Cadastro realizado com sucesso!</span>`;
                formCadastro.reset();
            } else {
                const error = await resp.text();
                msgDiv.innerHTML = `<span style="color:#c62828;">Erro ao cadastrar: ${error}</span>`;
            }
        } catch (err) {
            msgDiv.innerHTML = `<span style="color:#c62828;">Falha de conexão com o servidor!</span>`;
        }
    });
}
