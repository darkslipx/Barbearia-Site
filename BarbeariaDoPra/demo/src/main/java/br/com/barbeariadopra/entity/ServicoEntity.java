package br.com.barbeariadopra.entity;

import jakarta.persistence.*;
import lombok.*;

// Entidade que representa um serviço (ex: corte de cabelo, barba, etc.)
@Entity
@Table(name = "tbservico") // Define o nome da tabela no banco de dados
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok: gera getters/setters e construtores
public class ServicoEntity {

    // Identificador único do serviço (PK, autoincremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServico;

    // Nome do serviço (obrigatório)
    @Column(nullable = false)
    private String nome;

    // Valor do serviço (obrigatório)
    @Column(nullable = false)
    private Double valor;
}
