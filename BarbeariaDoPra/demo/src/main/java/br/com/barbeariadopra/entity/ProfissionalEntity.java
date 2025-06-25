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

// Entidade que representa um profissional (ex: barbeiro) no sistema
@Entity
@Table(name = "tbprofissionais") // Define o nome da tabela no banco de dados
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok: gera getters/setters e construtores
public class ProfissionalEntity {

    // Identificador único do profissional (PK, autoincremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProfissional", nullable = false)
    private Integer idProfissional;

    // Relação 1-para-1 com PessoaEntity, o profissional sempre está vinculado a uma pessoa
    // CascadeType.PERSIST: ao persistir Profissional, persiste Pessoa associada automaticamente
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pessoaId", referencedColumnName = "idPessoa", nullable = false)
    private PessoaEntity pessoa;

    // Telefone do profissional (obrigatório, até 20 caracteres)
    @Column(name = "telefone", length = 20, nullable = false)
    private String telefone;

    // Data de nascimento do profissional (obrigatório)
    @Column(name = "dataNascimento", nullable = false)
    private LocalDate dataNascimento;
}
