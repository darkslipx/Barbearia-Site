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

@Entity
@Table(name = "tbclientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCliente", nullable = false)
    private Integer idCliente;

    // ClienteEntity.java
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pessoaId", referencedColumnName = "idPessoa", nullable = false)
    private PessoaEntity pessoa;


    @Column(name = "telefone", length = 20, nullable = false)
    private String telefone;

    @Column(name = "dataNascimento", nullable = false)
    private LocalDate dataNascimento;
}