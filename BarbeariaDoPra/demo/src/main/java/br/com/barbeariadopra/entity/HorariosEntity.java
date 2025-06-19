package br.com.barbeariadopra.entity;



import java.time.LocalTime;

import br.com.barbeariadopra.enums.DiaSemana;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbhorarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HorariosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHorario", nullable = false)
    private Integer idHorario;

    @Column(name = "bloqueado", nullable = false)
    private Boolean bloqueado = true;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

        @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;


    @ManyToOne
    @JoinColumn(name = "profissionalId", referencedColumnName = "idProfissional", nullable = false)
    private ProfissionalEntity profissional;
}