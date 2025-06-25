const API_URL = "http://localhost:8080";

// Aguarda o carregamento do DOM
document.addEventListener("DOMContentLoaded", function () {
    const formLogin = document.getElementById("login-form");
    const loginMsg = document.getElementById("login-msg");

    if (formLogin) {
        // Ao enviar o formulário de login...
        formLogin.addEventListener('submit', async function (e) {
            e.preventDefault();

            // Coleta os dados do form
            const email = document.getElementById("email").value.trim();
            const senha = document.getElementById("password").value;

            try {
                // Faz a requisição de login para o backend
                const resp = await fetch(`${API_URL}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, senha })
                });

                if (resp.ok) {
                    // Se sucesso, extrai os dados do usuário
                    const data = await resp.json();
                    localStorage.setItem("usuarioNome", data.nome || "");
                    localStorage.setItem("usuarioEmail", data.email || "");
                    localStorage.setItem("usuarioNivel", data.nivel || "");

                    // Pega o nível (CLIENTE, PROFISSIONAL ou ADMIN)
                    const nivel = (data.nivel || "").toUpperCase();

                    // Se for cliente, busca o idCliente pelo idPessoa (data.id)
                    if (nivel === "CLIENTE") {
                        try {
                            const clienteResp = await fetch(`${API_URL}/clientes/pessoa/${data.id}`);
                            if (clienteResp.ok) {
                                const clienteData = await clienteResp.json();
                                localStorage.setItem("clienteId", clienteData.idCliente);
                                localStorage.setItem("usuarioId", data.id); // idPessoa
                            } else {
                                localStorage.removeItem("clienteId");
                            }
                        } catch {
                            localStorage.removeItem("clienteId");
                        }
                    } else {
                        // PROFISSIONAL e ADMIN só usam usuarioId
                        localStorage.setItem("usuarioId", data.id || "");
                        localStorage.removeItem("clienteId");
                    }

                    // Redireciona para a página conforme o nível
                    if (nivel === "PROFISSIONAL" || nivel === "ADMIN") {
                        window.location.href = "admin.html";
                    } else {
                        window.location.href = "cliente.html";
                    }
                } else {
                    // Login inválido
                    loginMsg.textContent = "E-mail ou senha incorretos.";
                }
            } catch (err) {
                // Falha de conexão
                loginMsg.textContent = "Falha ao conectar com o servidor!";
            }
        });
    }
});
