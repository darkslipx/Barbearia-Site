const API_URL = "http://localhost:8080"; // URL da sua API backend

// Função auxiliar para obter o ID do usuário logado (sempre idPessoa)
function getUsuarioId() {
    return localStorage.getItem("usuarioId") || null;
}

// Função auxiliar para obter o nível do usuário logado (CLIENTE ou PROFISSIONAL)
function getUsuarioNivel() {
    return (localStorage.getItem("usuarioNivel") || "").toUpperCase();
}

// Variável global para armazenar os dados originais carregados do backend.
// Usada para comparar se houve alteração ao salvar.
let dadosOriginais = {};

// Ao carregar a página, exibe o nome do usuário no header e atualiza a foto do perfil conforme o nível
document.addEventListener('DOMContentLoaded', function () {
    const nome = localStorage.getItem('usuarioNome') || 'Usuário'; // Nome salvo no localStorage (login)
    const nivel = getUsuarioNivel(); // Nível do usuário logado (CLIENTE ou PROFISSIONAL)
    const nomeElemento = document.getElementById('nome-usuario-header'); // Elemento de nome no topo
    const profilePic = document.querySelector('.profile-pic'); // Elemento da foto do perfil

    if (nomeElemento) {
        nomeElemento.textContent = nome; // Mostra o nome no topo
    }
    if (profilePic) {
        if (nivel === 'PROFISSIONAL') {
            profilePic.src = 'img/barbeiro.png'; // Foto padrão de profissional
        } else {
            profilePic.src = 'img/perfil-cliente.png'; // Foto padrão de cliente
        }
    }
});

// Função principal que busca e preenche os dados do perfil (cliente ou profissional)
async function carregarPerfil() {
    const idPessoa = getUsuarioId(); // idPessoa salvo no localStorage
    const nivel = getUsuarioNivel(); // Nível do usuário logado

    if (!idPessoa) return; // Se não tem idPessoa, não faz nada (usuário não logado?)

    try {
        let resp; // Response da requisição
        // Se for CLIENTE, busca pelo endpoint de cliente usando o idPessoa
        if (nivel === "CLIENTE") {
            resp = await fetch(`${API_URL}/clientes/pessoa/${idPessoa}`);
        }
        // Se for PROFISSIONAL, busca pelo endpoint de profissional usando idProfissional salvo no localStorage
        else if (nivel === "PROFISSIONAL") {
            const idProfissional = localStorage.getItem("idProfissional");
            if (!idProfissional) {
                // Se não tem idProfissional no localStorage, exibe mensagem de erro
                const msgElem = document.getElementById('profile-msg');
                if (msgElem) msgElem.innerText = "Profissional não encontrado!";
                return;
            }
            // Busca o profissional pelo idProfissional
            resp = await fetch(`${API_URL}/profissional/${idProfissional}`);
        }
        else {
            // Nível de usuário desconhecido
            const msgElem = document.getElementById('profile-msg');
            if (msgElem) msgElem.innerText = "Nível de usuário desconhecido.";
            return;
        }

        // Se não encontrou o usuário/profissional
        if (!resp.ok) {
            const msgElem = document.getElementById('profile-msg');
            if (msgElem) msgElem.innerText = `${nivel} não encontrado!`;
            return;
        }

        // Carrega os dados vindos da API
        const dados = await resp.json();
        dadosOriginais = dados; // Salva dados para comparar depois

        // Preenche o formulário do perfil com os dados
        if (document.getElementById('nome'))      document.getElementById('nome').value      = dados.pessoa?.nome || "";
        if (document.getElementById('email'))     document.getElementById('email').value     = dados.pessoa?.email || "";
        if (document.getElementById('telefone'))  document.getElementById('telefone').value  = dados.telefone || "";
        if (document.getElementById('nascimento'))document.getElementById('nascimento').value= dados.dataNascimento || "";

        // Para clientes, garante que o idCliente está salvo no localStorage
        if (nivel === "CLIENTE") {
            localStorage.setItem("clienteId", dados.idCliente);
        }
    } catch (e) {
        // Em caso de erro de requisição
        const msgElem = document.getElementById('profile-msg');
        if (msgElem) msgElem.innerText = "Erro ao carregar perfil.";
    }
}

// Evento do formulário de atualização de perfil (quando o usuário clica em Salvar)
document.getElementById('profile-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const msg = document.getElementById('profile-msg');
    msg.innerText = ""; // Limpa mensagem

    const nivel = getUsuarioNivel();

    // Pega os valores preenchidos pelo usuário no formulário
    const nome = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    const nascimento = document.getElementById('nascimento').value;

    // Monta os objetos para atualização (envia só o que mudou)
    const bodyPessoa = {};
    const bodyUsuario = {};

    if (nome && nome !== (dadosOriginais.pessoa?.nome || "")) bodyPessoa.nome = nome;
    if (email && email !== (dadosOriginais.pessoa?.email || "")) bodyPessoa.email = email;

    if (telefone && telefone !== (dadosOriginais.telefone || "")) bodyUsuario.telefone = telefone;
    if (nascimento && nascimento !== (dadosOriginais.dataNascimento || "")) bodyUsuario.dataNascimento = nascimento;

    try {
        // Atualiza os dados da tabela pessoa (nome e email)
        if (Object.keys(bodyPessoa).length > 0) {
            const respPessoa = await fetch(`${API_URL}/pessoa/${dadosOriginais.pessoa.idPessoa}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(bodyPessoa)
            });
            if (!respPessoa.ok) throw new Error("Erro ao atualizar dados pessoais");
        }

        // Atualiza os dados específicos de cliente ou profissional
        if (Object.keys(bodyUsuario).length > 0) {
            let urlUpdate = "";
            if (nivel === "CLIENTE") {
                urlUpdate = `${API_URL}/clientes/${dadosOriginais.idCliente}`;
            } else if (nivel === "PROFISSIONAL") {
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

    // Valida e atualiza a senha se o usuário preencheu algum dos campos de senha
    const senhaAtual = document.getElementById('senha-atual').value;
    const novaSenha = document.getElementById('nova-senha').value;
    const confirmaSenha = document.getElementById('confirma-senha').value;

    if (senhaAtual || novaSenha || confirmaSenha) {
        // Se algum dos campos está preenchido, exige todos os campos
        if (!senhaAtual || !novaSenha || !confirmaSenha) {
            msg.style.color = "red";
            msg.innerText = "Preencha todos os campos de senha para alterar.";
            return;
        }
        // Confirma se a nova senha é igual à confirmação
        if (novaSenha !== confirmaSenha) {
            msg.style.color = "red";
            msg.innerText = "Nova senha e confirmação não conferem!";
            return;
        }
        try {
            // Envia atualização de senha para o backend
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

    // Mostra mensagem de sucesso
    msg.style.color = "green";
    msg.innerText = "Perfil atualizado!";
    // Recarrega perfil atualizado do backend
    await carregarPerfil();
});

// Ao abrir a página, carrega os dados do perfil automaticamente
document.addEventListener('DOMContentLoaded', carregarPerfil);
