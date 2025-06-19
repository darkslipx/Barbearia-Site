const API_URL = "http://localhost:8080";

// Pega o ID do cliente logado
function getClienteId() {
    // Ajude o login a salvar isso! Ajuste conforme salvar no localStorage
    return localStorage.getItem("usuarioId") || localStorage.getItem("clienteId") || null;
}

let dadosOriginais = {};

document.addEventListener('DOMContentLoaded', function () {
    const nome = localStorage.getItem('usuarioNome') || 'Usuário';
    document.getElementById('nome-usuario').textContent = nome;
});

async function carregarPerfil() {
    // Pega o idPessoa do usuário logado
    const idPessoa = localStorage.getItem('usuarioId');
    if (!idPessoa) return;

    try {
        // Busca o cliente pelo idPessoa!
        const resp = await fetch(`${API_URL}/clientes/pessoa/${idPessoa}`);
        if (!resp.ok) {
            document.getElementById('profile-msg').innerText = "Cliente não encontrado!";
            return;
        }
        const dados = await resp.json();
        dadosOriginais = dados;
        document.getElementById('nome').value = dados.pessoa?.nome || "";
        document.getElementById('email').value = dados.pessoa?.email || "";
        document.getElementById('telefone').value = dados.telefone || "";
        document.getElementById('nascimento').value = dados.dataNascimento || "";
        localStorage.setItem("clienteId", dados.idCliente); // Atualiza o clienteId!
    } catch (e) {
        document.getElementById('profile-msg').innerText = "Erro ao carregar perfil.";
    }
}


document.getElementById('profile-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const msg = document.getElementById('profile-msg');
    msg.innerText = "";

    // Só envia o que mudou (pode mandar tudo também, se preferir)
    const nome = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    const nascimento = document.getElementById('nascimento').value;

    const bodyCliente = {};
    const bodyPessoa = {};

    if (telefone && telefone !== (dadosOriginais.telefone || "")) bodyCliente.telefone = telefone;
    if (nascimento && nascimento !== (dadosOriginais.dataNascimento || "")) bodyCliente.dataNascimento = nascimento;

    if (nome && nome !== (dadosOriginais.pessoa?.nome || "")) bodyPessoa.nome = nome;
    if (email && email !== (dadosOriginais.pessoa?.email || "")) bodyPessoa.email = email;

    // Atualiza CLIENTE (telefone, nascimento)
    if (Object.keys(bodyCliente).length > 0) {
        try {
            const resp = await fetch(`${API_URL}/clientes/${dadosOriginais.idCliente}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ ...bodyCliente })
            });
            if (!resp.ok) throw new Error();
        } catch {
            msg.style.color = "red";
            msg.innerText = "Erro ao atualizar dados do cliente.";
            return;
        }
    }
    // Atualiza PESSOA (nome, email)
    if (Object.keys(bodyPessoa).length > 0) {
        try {
            const resp = await fetch(`${API_URL}/pessoa/${dadosOriginais.pessoa.idPessoa}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ ...bodyPessoa })
            });
            if (!resp.ok) throw new Error();
        } catch {
            msg.style.color = "red";
            msg.innerText = "Erro ao atualizar dados pessoais.";
            return;
        }
    }

    // Campos de senha
    const senhaAtual = document.getElementById('senha-atual').value;
    const novaSenha = document.getElementById('nova-senha').value;
    const confirmaSenha = document.getElementById('confirma-senha').value;


    // --- Troca de senha ---
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
            // Limpa campos de senha sempre ao abrir/atualizar o perfil!
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

// Chama ao abrir página:
document.addEventListener('DOMContentLoaded', carregarPerfil);
