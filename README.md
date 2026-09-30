# 💈 Barbearia do Pra — Sistema de Agendamento Online

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL%20Server-CC2927?logo=microsoftsqlserver&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?logo=javascript&logoColor=black)

Sistema web full stack para barbearias: o cliente agenda horário escolhendo profissional, serviço, data e horário livre; a administração gerencia agenda, profissionais, clientes, serviços e horários de funcionamento.

Projeto acadêmico (Projeto Interdisciplinar — Fatec) desenvolvido em equipe.

## Sumário

- [Funcionalidades](#-funcionalidades)
- [Stack](#️-stack)
- [Arquitetura](#-arquitetura)
- [API REST](#-api-rest)
- [Modelo de dados](#️-modelo-de-dados)
- [Como executar](#️-como-executar)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Equipe](#-equipe)

## ✨ Funcionalidades

**Cliente**
- Cadastro, login e recuperação de senha
- Agendamento em etapas: profissional → serviço → dia → horário disponível
- "Meus agendamentos": acompanhamento de status (pendente, confirmado, recusado, cancelado) e cancelamento
- Edição de perfil

**Administrador**
- Painel de agendamentos com filtros, confirmação e recusa
- CRUD de profissionais, clientes e serviços
- Cadastro dos horários de funcionamento por profissional e dia da semana, com **geração automática de horários**
- Bloqueio/desbloqueio de horários específicos e horários extras

## 🛠️ Stack

| Camada | Tecnologias |
|---|---|
| Back-end | Java 17, Spring Boot 3.5 (Web, Data JPA), Lombok, Maven |
| Banco de dados | Microsoft SQL Server (JPA/Hibernate) |
| Front-end | HTML5, CSS3, JavaScript puro (Fetch API), servido pelo próprio Spring Boot |

## 🧱 Arquitetura

Arquitetura em camadas, padrão MVC/REST:

```
Navegador (HTML/CSS/JS)
      │  fetch (JSON)
      ▼
Controller  →  Service  →  Repository (Spring Data JPA)  →  SQL Server
      ▲            │
      └── DTOs ────┘        Entities mapeadas com JPA
```

- **controller/** — endpoints REST
- **service/** — regras de negócio (disponibilidade de horários, conflitos, status do agendamento)
- **repository/** — acesso a dados com Spring Data JPA
- **entity/**, **dto/**, **enums/** — modelo de domínio e objetos de transferência

## 🔌 API REST

| Recurso | Principais endpoints |
|---|---|
| `/login` | `POST` autenticação |
| `/pessoa` | CRUD + `PUT /{id}/senha` |
| `/clientes` | CRUD, `POST /admin` (cadastro pelo administrador) |
| `/profissional` | CRUD de profissionais |
| `/servicos` | CRUD de serviços |
| `/funcionamento` | CRUD, `GET /profissional/{id}/dias-disponiveis`, `GET /profissional/{id}/dia/{dia}/horarios-disponiveis`, `POST /{id}/gerar-horarios` |
| `/horario` | CRUD, `GET /disponiveis`, `GET /bloqueados`, `GET /com-status`, `PUT /{id}/bloquear`, `PUT /{id}/desbloquear` |
| `/agendamentos` | CRUD, `GET /cliente/{id}`, `GET /horarios-disponiveis`, `PUT /{id}/cancelar` |

## 🗄️ Modelo de dados

Tabelas principais (script em [`BarbeariaDoPra/ScriptBanco.sql`](BarbeariaDoPra/ScriptBanco.sql)):

`tbpessoas` · `tbclientes` · `tbprofissionais` · `tbservico` · `tbfuncionamento` · `tbhorarios` · `tbagendamentos`

## ▶️ Como executar

**Pré-requisitos:** Java 17+, SQL Server (Maven é opcional — o projeto inclui o Maven Wrapper).

```bash
# 1. Clone o repositório
git clone https://github.com/darkslipx/Barbearia-Site.git
cd Barbearia-Site/BarbeariaDoPra

# 2. Crie o banco executando ScriptBanco.sql no SQL Server (banco: bdBarbearia)

# 3. Ajuste a conexão em demo/src/main/resources/application.properties
#    spring.datasource.url / username / password

# 4. Suba a aplicação
cd demo
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Acesse **http://localhost:8080** — o front-end é servido a partir de `src/main/resources/static`.

## 📁 Estrutura do projeto

```
BarbeariaDoPra/
├── ScriptBanco.sql                  # criação do banco e das tabelas
└── demo/                            # projeto Spring Boot
    ├── pom.xml
    └── src/main/
        ├── java/br/com/barbeariadopra/
        │   ├── controller/          # endpoints REST
        │   ├── service/             # regras de negócio
        │   ├── repository/          # Spring Data JPA
        │   ├── entity/              # entidades JPA
        │   ├── dto/
        │   └── enums/
        └── resources/
            ├── application.properties
            └── static/              # front-end (HTML, CSS, JS, imagens)
                ├── index.html, login.html, cadastro.html, recuperar.html
                ├── cliente.html, meus-agendamentos.html, perfil.html
                ├── admin.html, cadastroadmin.html, horarios.html
                └── js/
```

## 👥 Equipe

- **Abner Evandro Duarte** — [@darkslipx](https://github.com/darkslipx)
- Pedro Thiago Campus
- Vitor Natti Salgado
- Mateus Henrique dos Santos Pereira

## 📄 Licença

Projeto desenvolvido para fins acadêmicos e de estudo.
