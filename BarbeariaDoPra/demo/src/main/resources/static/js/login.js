const API_URL = "http://localhost:8080";

// Aguarda o carregamento do DOM
document.addEventListener("DOMContentLoaded", function () {
    const formLogin = document.getElementById("login-form");
    const loginMsg = document.getElementById("login-msg");

    if (formLogin) {
        formLogin.addEventListener('submit', async function (e) {
            e.preventDefault();

            const email = document.getElementById("email").value.trim();
            const senha = document.getElementById("password").value;

            try {
                const resp = await fetch(`${API_URL}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, senha })
                });

                if (resp.ok) {
                    const data = await resp.json();
                    localStorage.setItem("usuarioNome", data.nome || "");
                    localStorage.setItem("usuarioEmail", data.email || "");
                    localStorage.setItem("usuarioNivel", data.nivel || "");

                    const nivel = (data.nivel || "").toUpperCase();

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
                        window.location.href = "cliente.html";
                    } else if (nivel === "PROFISSIONAL") {
                        // Salva o idPessoa do usuário (usuarioId)
                        localStorage.setItem("usuarioId", data.id || "");
                        localStorage.removeItem("clienteId");

                        // Salva o idProfissional RETORNADO do backend direto!
                        if (data.idProfissional) {
                            localStorage.setItem("idProfissional", data.idProfissional);
                        } else {
                            localStorage.removeItem("idProfissional");
                        }

                        window.location.href = "admin.html";
                    } else {
                        loginMsg.textContent = "Tipo de usuário não suportado.";
                    }
                } else {
                    loginMsg.textContent = "E-mail ou senha incorretos.";
                }
            } catch (err) {
                loginMsg.textContent = "Falha ao conectar com o servidor!";
            }
        });
    }
});
