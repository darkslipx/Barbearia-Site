package br.com.barbeariadopra.entity;

import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

import br.com.barbeariadopra.enums.DiaSemana;

@Entity
@Table(name = "tbfuncionamento") // Define o nome da tabela no banco de dados
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok: gera getters, setters e construtores
public class FuncionamentoEntity {

    // Identificador único do registro de funcionamento (PK, autoincremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Enum que representa o dia da semana (armazenado como String no banco)
    @Enumerated(EnumType.STRING)
    @Column(name = "diaSemana", length = 20, nullable = false)
    private DiaSemana diaSemana;

    // Relação muitos-para-um: cada funcionamento pertence a um profissional
    @ManyToOne
    @JoinColumn(name = "profissionalId", referencedColumnName = "idProfissional", nullable = false)
    private ProfissionalEntity profissional;

    // Hora de início do expediente nesse dia da semana
    @Column(name = "horaInicio", length = 20, nullable = false)
    private LocalTime horaInicio;

    // Hora de fim do expediente nesse dia da semana
    @Column(name = "horaFim", length = 20, nullable = false)
    private LocalTime horaFim;

    // Informação extra sobre disponibilidade do horário (pode ser uma faixa ou status)
    @Column(name = "horario_disponivel", length = 40, nullable = false)
    private String horarioDisponivel;
}

