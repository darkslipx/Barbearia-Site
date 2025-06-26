const API_URL = "http://localhost:8080";

// Seleciona o formulário de cadastro (página de cadastro de cliente)
const formCadastro = document.getElementById('form-cadastro');

// Verifica se o formulário existe na página antes de adicionar evento
if (formCadastro) {
    // Evento de submit do formulário
    formCadastro.addEventListener('submit', async function (e) {
        e.preventDefault(); // Evita o reload padrão do form

        // Captura os valores dos campos
        const nome = document.getElementById('primeiro-nome').value.trim();
        const sobrenome = document.getElementById('ultimo-nome').value.trim();
        const email = document.getElementById('email').value.trim();
        const telefone = document.getElementById('telefone').value.trim();
        const dataNascimento = document.getElementById('data-nascimento').value;
        const senha = document.getElementById('password').value;
        const confirmarSenha = document.getElementById('confirmar-password').value;
        // Área para mensagens de retorno (sucesso/erro)
        const msgDiv = document.getElementById('msg-cadastro') || document.createElement("div");

        // Validação: Confere se as senhas batem
        if (senha !== confirmarSenha) {
            msgDiv.innerHTML = `<span style="color:#c62828;">As senhas não coincidem!</span>`;
            return;
        }

        // Monta objeto Pessoa conforme esperado pelo backend
        const pessoa = {
            nome: nome + ' ' + sobrenome, // Junta nome + sobrenome
            email: email,
            nivel: "CLIENTE", // Nível sempre CLIENTE
            senha: senha      // Senha (se for persistida em PessoaEntity)
            // Não adiciona dataNascimento se não existir em PessoaEntity
        };
        // Monta objeto Cliente conforme esperado pelo backend
        const cliente = {
            pessoa: pessoa,
            telefone: telefone,
            dataNascimento: dataNascimento // Só aqui se faz parte de ClienteEntity
        };

        // Envio da requisição POST
        try {
            const resp = await fetch(`${API_URL}/clientes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(cliente)
            });
            // Se cadastro ok
            if (resp.ok) {
                msgDiv.innerHTML = `<span style="color:green;">Cadastro realizado com sucesso!</span>`;
                formCadastro.reset();
                window.location.href = "login.html";
            } else {
                // Se houver erro, exibe a mensagem retornada pelo backend
                const error = await resp.text();
                msgDiv.innerHTML = `<span style="color:#c62828;">Erro ao cadastrar: ${error}</span>`;
            }
        } catch (err) {
            // Falha de conexão (API offline, por exemplo)
            msgDiv.innerHTML = `<span style="color:#c62828;">Falha de conexão com o servidor!</span>`;
        }
    });
}
