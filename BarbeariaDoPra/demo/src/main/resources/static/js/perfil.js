const API_URL = "http://localhost:8080";

// Função para pegar o ID do usuário logado
function getUsuarioId() {
    return localStorage.getItem("usuarioId") || null;
}

// Função para pegar o nível do usuário logado
function getUsuarioNivel() {
    return (localStorage.getItem("usuarioNivel") || "").toUpperCase();
}

let dadosOriginais = {}; // Armazena os dados atuais para comparação

// Ao carregar a página, exibe o nome do usuário no header e atualiza a foto padrão conforme nível
document.addEventListener('DOMContentLoaded', function () {
    const nome = localStorage.getItem('usuarioNome') || 'Usuário';
    const nivel = getUsuarioNivel();
    const nomeElemento = document.getElementById('nome-usuario-header');
    const profilePic = document.querySelector('.profile-pic');

    if (nomeElemento) {
        nomeElemento.textContent = nome;
    }

    if (profilePic) {
        if (nivel === 'PROFISSIONAL') {
            profilePic.src = 'img/barbeiro.png'; // Caminho da foto padrão profissional
        } else {
            profilePic.src = 'img/perfil-cliente.png'; // Caminho da foto padrão cliente
        }
    }
});

// Função que busca os dados do perfil conforme nível e idPessoa
async function carregarPerfil() {
    const idPessoa = getUsuarioId();
    const nivel = getUsuarioNivel();
    if (!idPessoa) return;

    try {
        let resp;
        if (nivel === "CLIENTE") {
            // Busca dados de cliente
            resp = await fetch(`${API_URL}/clientes/pessoa/${idPessoa}`);
        } else if (nivel === "PROFISSIONAL" || nivel === "ADMIN") {
            // Busca dados de profissional/admin
            resp = await fetch(`${API_URL}/profissional/${idPessoa}`);
        } else {
            // Se nível desconhecido, para aqui
            const msgElem = document.getElementById('profile-msg');
            if (msgElem) msgElem.innerText = "Nível de usuário desconhecido.";
            return;
        }

        if (!resp.ok) {
            const msgElem = document.getElementById('profile-msg');
            if (msgElem) msgElem.innerText = `${nivel} não encontrado!`;
            return;
        }

        const dados = await resp.json();
        dadosOriginais = dados;

        // Preenche os campos do formulário, que têm a mesma estrutura para ambos
        if (document.getElementById('nome')) document.getElementById('nome').value = dados.pessoa?.nome || "";
        if (document.getElementById('email')) document.getElementById('email').value = dados.pessoa?.email || "";
        if (document.getElementById('telefone')) document.getElementById('telefone').value = dados.telefone || "";
        if (document.getElementById('nascimento')) document.getElementById('nascimento').value = dados.dataNascimento || "";

        // Atualiza clienteId para clientes no localStorage
        if (nivel === "CLIENTE") {
            localStorage.setItem("clienteId", dados.idCliente);
        }
    } catch (e) {
        const msgElem = document.getElementById('profile-msg');
        if (msgElem) msgElem.innerText = "Erro ao carregar perfil.";
    }
}

// Evento de envio do formulário de atualização de perfil
document.getElementById('profile-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const msg = document.getElementById('profile-msg');
    msg.innerText = "";

    const nivel = getUsuarioNivel();

    // Pega valores dos campos do formulário
    const nome = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    const nascimento = document.getElementById('nascimento').value;

    const bodyPessoa = {};
    const bodyUsuario = {}; // Pode ser cliente ou profissional

    // Só preenche se mudou
    if (nome && nome !== (dadosOriginais.pessoa?.nome || "")) bodyPessoa.nome = nome;
    if (email && email !== (dadosOriginais.pessoa?.email || "")) bodyPessoa.email = email;

    if (telefone && telefone !== (dadosOriginais.telefone || "")) bodyUsuario.telefone = telefone;
    if (nascimento && nascimento !== (dadosOriginais.dataNascimento || "")) bodyUsuario.dataNascimento = nascimento;

    try {
        // Atualiza dados PESSOA
        if (Object.keys(bodyPessoa).length > 0) {
            const respPessoa = await fetch(`${API_URL}/pessoa/${dadosOriginais.pessoa.idPessoa}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(bodyPessoa)
            });
            if (!respPessoa.ok) throw new Error("Erro ao atualizar dados pessoais");
        }

        // Atualiza dados CLIENTE ou PROFISSIONAL/ADMIN
        if (Object.keys(bodyUsuario).length > 0) {
            let urlUpdate = "";
            if (nivel === "CLIENTE") {
                urlUpdate = `${API_URL}/clientes/${dadosOriginais.idCliente}`;
            } else if (nivel === "PROFISSIONAL" || nivel === "ADMIN") {
                urlUpdate = `${API_URL}/profissional/${dadosOriginais.idProfissional}`;
            }

            if (urlUpdate) {
                const respUsuario = await fetch(urlUpdate, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(bodyUsuario)
                });
                if (!respUsuario.ok) throw new Error("Erro ao atualizar dados do usuário");
            }
        }
    } catch {
        msg.style.color = "red";
        msg.innerText = "Erro ao atualizar perfil.";
        return;
    }

    // Troca de senha
    const senhaAtual = document.getElementById('senha-atual').value;
    const novaSenha = document.getElementById('nova-senha').value;
    const confirmaSenha = document.getElementById('confirma-senha').value;

    if (senhaAtual || novaSenha || confirmaSenha) {
        if (!senhaAtual || !novaSenha || !confirmaSenha) {
            msg.style.color = "red";
            msg.innerText = "Preencha todos os campos de senha para alterar.";
            return;
        }
        if (novaSenha !== confirmaSenha) {
            msg.style.color = "red";
            msg.innerText = "Nova senha e confirmação não conferem!";
            return;
        }
        try {
            const resp = await fetch(`${API_URL}/pessoa/${dadosOriginais.pessoa.idPessoa}/senha`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ senhaAtual, novaSenha })
            });
            if (!resp.ok) throw new Error();
            msg.style.color = "green";
            msg.innerText = "Senha alterada com sucesso!";
            // Limpa campos de senha após sucesso
            document.getElementById('senha-atual').value = '';
            document.getElementById('nova-senha').value = '';
            document.getElementById('confirma-senha').value = '';
        } catch {
            msg.style.color = "red";
            msg.innerText = "Erro ao alterar senha. Verifique a senha atual.";
            return;
        }
    }

    msg.style.color = "green";
    msg.innerText = "Perfil atualizado!";
    await carregarPerfil();
});

// Carrega perfil ao abrir página
document.addEventListener('DOMContentLoaded', carregarPerfil);
