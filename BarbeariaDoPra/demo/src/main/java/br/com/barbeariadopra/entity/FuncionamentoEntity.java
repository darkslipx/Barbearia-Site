package br.com.barbeariadopra.entity;

import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

import br.com.barbeariadopra.enums.DiaSemana;

@Entity
@Table(name = "tbfuncionamento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FuncionamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "diaSemana", length = 20, nullable = false)
    private DiaSemana diaSemana;

    @ManyToOne
    @JoinColumn(name = "profissionalId", referencedColumnName = "idProfissional", nullable = false)
    private ProfissionalEntity profissional;

    @Column(name = "horaInicio", length = 20, nullable = false)
    private LocalTime horaInicio;

    @Column(name = "horaFim", length = 20, nullable = false)
    private LocalTime horaFim;

    @Column(name = "horario_disponivel", length = 40, nullable = false)
    private String horarioDisponivel;
}
