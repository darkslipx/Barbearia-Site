package br.com.barbeariadopra.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbservico")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ServicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServico;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double valor;
}
