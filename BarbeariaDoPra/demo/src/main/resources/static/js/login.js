const API_URL = "http://localhost:8080";

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
                        // BUSCA o cliente pelo idPessoa!
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
                        localStorage.setItem("usuarioId", data.id || "");
                        localStorage.removeItem("clienteId");
                    }

                    if (nivel === "PROFISSIONAL" || nivel === "ADMIN") {
                        window.location.href = "admin.html";
                    } else {
                        window.location.href = "cliente.html";
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
