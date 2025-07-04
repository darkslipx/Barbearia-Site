USE [bdBarbearia];
GO

-- Criação das tabelas principais do sistema da barbearia

-- Tabela de pessoas (clientes ou profissionais)
CREATE TABLE tbpessoas (
    id_pessoa INT IDENTITY(1,1) PRIMARY KEY,
    nivel VARCHAR(20) NOT NULL,           -- Ex: CLIENTE, PROFISSIONAL, ADMIN
    senha VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    nome VARCHAR(200) NOT NULL
);

-- Tabela de clientes
CREATE TABLE tbclientes (
    id_cliente INT IDENTITY(1,1) PRIMARY KEY,
    pessoa_id INT NOT NULL UNIQUE,        -- Chave única para evitar duplicidade de pessoa
    telefone VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    FOREIGN KEY (pessoa_id) REFERENCES tbpessoas(id_pessoa)
);

-- Tabela de profissionais
CREATE TABLE tbprofissionais (
    id_profissional INT IDENTITY(1,1) PRIMARY KEY,
    pessoa_id INT NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    FOREIGN KEY (pessoa_id) REFERENCES tbpessoas(id_pessoa)
);

-- Tabela de serviços
CREATE TABLE tbservico (
    id_servico INT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    valor FLOAT NOT NULL
);

-- Tabela de horários de funcionamento (disponibilidade do profissional)
CREATE TABLE tbfuncionamento (
    id INT IDENTITY(1,1) PRIMARY KEY,
    profissional_id INT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL CHECK (
        dia_semana IN ('SEGUNDA_FEIRA', 'TERCA_FEIRA', 'QUARTA_FEIRA', 'QUINTA_FEIRA', 'SEXTA_FEIRA', 'SABADO', 'DOMINGO')
    ),
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    horario_disponivel VARCHAR(40) NOT NULL,
    FOREIGN KEY (profissional_id) REFERENCES tbprofissionais(id_profissional)
);

-- Tabela de horários para agendamento
CREATE TABLE tbhorarios (
    id_horario INT IDENTITY(1,1) PRIMARY KEY,
    profissional_id INT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL CHECK (
        dia_semana IN ('SEGUNDA_FEIRA', 'TERCA_FEIRA', 'QUARTA_FEIRA', 'QUINTA_FEIRA', 'SEXTA_FEIRA', 'SABADO', 'DOMINGO')
    ),
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    bloqueado BIT NOT NULL,
    FOREIGN KEY (profissional_id) REFERENCES tbprofissionais(id_profissional)
);

-- Tabela de agendamentos (vincula cliente, profissional, horário, serviço)
CREATE TABLE tbagendamentos (
    id_agendamento INT IDENTITY(1,1) PRIMARY KEY,
    cliente_id INT NOT NULL,
    profissional_id INT NOT NULL,
    servico_id INT,
    horario_id INT NOT NULL,
    data DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES tbclientes(id_cliente),
    FOREIGN KEY (profissional_id) REFERENCES tbprofissionais(id_profissional),
    FOREIGN KEY (servico_id) REFERENCES tbservico(id_servico),
    FOREIGN KEY (horario_id) REFERENCES tbhorarios(id_horario)
);
