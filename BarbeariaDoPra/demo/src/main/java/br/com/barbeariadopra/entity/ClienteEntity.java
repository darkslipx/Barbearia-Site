package br.com.barbeariadopra.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entidade que representa um cliente no sistema
@Entity
@Table(name = "tbclientes") // Define o nome da tabela no banco de dados
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok: gera getters/setters e construtores
public class ClienteEntity {

    // Identificador único do cliente (PK, autoincremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCliente", nullable = false)
    private Integer idCliente;

    // Relação 1-para-1 com PessoaEntity, o cliente sempre está vinculado a uma pessoa
    // CascadeType.PERSIST: ao persistir Cliente, persiste Pessoa associada automaticamente
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pessoaId", referencedColumnName = "idPessoa", nullable = false)
    private PessoaEntity pessoa;

    // Telefone do cliente (não pode ser nulo, máximo 20 caracteres)
    @Column(name = "telefone", length = 20, nullable = false)
    private String telefone;

    // Data de nascimento do cliente (não pode ser nula)
    @Column(name = "dataNascimento", nullable = false)
    private LocalDate dataNascimento;
}
