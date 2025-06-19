# 💈 Barbearia do Pra – Sistema de Agendamento
Este repositório contém um sistema completo de agendamento online para barbearias, desenvolvido para facilitar o controle de horários, profissionais, clientes e serviços.
---

## 📚 Descrição
O sistema permite que clientes agendem horários com profissionais e serviços, enquanto a administração gerencia horários, cadastros, status dos agendamentos e realiza o cadastro/edição de profissionais e clientes. O objetivo é digitalizar o atendimento e otimizar o controle da barbearia.

---

## 🚀 Funcionalidades

- Cadastro e login de usuários (cliente e administrador)
- Painel administrativo: gerenciamento de agendamentos, profissionais, clientes, horários e serviços
- Agendamento online: escolha do profissional, serviço, data e horário disponível
- Bloqueio/desbloqueio de horários
- Cadastro de horários de funcionamento e horários extras
- Visualização dos status dos agendamentos (pendente, confirmado, recusado)
- Filtros avançados para busca de agendamentos, horários e clientes
- Interface simples, responsiva e intuitiva

---

## 🛠️ Tecnologias Utilizadas

- **Frontend:** HTML5, CSS3, JavaScript puro
- **Backend:** Java 17, Spring Boot
- **Banco de Dados:** SQL Server
- **Versionamento:** Git e GitHub
---

## 📁 Estrutura do Projeto

/
├── backend/ # Projeto Spring Boot (Java)
│ └── ... (código backend)
├── frontend/ # Aplicação web (HTML, CSS, JS)
│ ├── admin.html
│ ├── cadastro.html
│ ├── cadastroadmin.html
│ ├── cliente.html
│ ├── horarios.html
│ ├── index.html
│ ├── login.html
│ ├── perfil.html
│ ├── recuperar.html
│ ├── script.js
│ └── style.css
├── img/ # Imagens da interface
├── README.md
└── ...
---

## ⚙️ Como Executar o Projeto

### 1. Clone o repositório

```bash
git clone https://github.com/seuusuario/seurepositorio.git
cd seurepositorio
2. Configure e rode o backend
Requisitos: Java 17+, Maven, SQL Server rodando.

Configure a conexão do banco em application.properties no backend.

Crie o banco de dados utilizando o script SQL disponível.

No terminal ou IDE, execute:

bash
Copy
Edit
cd backend
./mvnw spring-boot:run
3. Rode o frontend
Basta abrir index.html ou admin.html dentro da pasta /frontend com seu navegador.

Os arquivos HTML, CSS e JS estão prontos para uso local.

📝 Scripts do Banco de Dados
O script de criação das tabelas e inserção de dados está disponível na pasta /backend/sql/ ou pode ser solicitado ao administrador do repositório.

👤 Autor
Nome: Seu Nome

E-mail: seu.email@exemplo.com

GitHub: seuusuario

📃 Licença
Projeto desenvolvido para fins acadêmicos e de estudo.
Sinta-se à vontade para utilizar, modificar e aprimorar!