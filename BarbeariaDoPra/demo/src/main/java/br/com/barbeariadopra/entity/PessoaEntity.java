package br.com.barbeariadopra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entidade que representa uma pessoa (pode ser cliente, profissional ou outro tipo de usuário)
@Entity
@Table(name = "tbpessoas") // Define o nome da tabela no banco de dados
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok: gera getters/setters e construtores
public class PessoaEntity {

    // Identificador único da pessoa (PK, autoincremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPessoa")
    private Integer idPessoa;

    // Nível de acesso ou papel da pessoa (ex: CLIENTE, PROFISSIONAL, ADMIN)
    @Column(name = "nivel", nullable = false, length = 20)
    private String nivel;

    // E-mail da pessoa (deve ser único, até 200 caracteres)
    @Column(name = "email", nullable = false, length = 200)
    private String email;

    // Senha da pessoa (até 100 caracteres, normalmente hash da senha)
    @Column(name = "senha", nullable = false, length = 100)
    private String senha;

    // Nome completo da pessoa (até 200 caracteres)
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;
}
