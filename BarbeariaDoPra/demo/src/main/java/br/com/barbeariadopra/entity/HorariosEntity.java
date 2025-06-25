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

// Entidade que representa os horários disponíveis para agendamento
@Entity
@Table(name = "tbhorarios") // Define o nome da tabela no banco de dados
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok: gera getters, setters e construtores
public class HorariosEntity {

    // Identificador único do horário (PK, autoincremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHorario", nullable = false)
    private Integer idHorario;

    // Indica se o horário está bloqueado para agendamento
    @Column(name = "bloqueado", nullable = false)
    private Boolean bloqueado = true;

    // Hora de início do horário
    @Column(nullable = false)
    private LocalTime horaInicio;

    // Hora de fim do horário
    @Column(nullable = false)
    private LocalTime horaFim;

    // Dia da semana do horário (enum armazenado como string)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;

    // Relação muitos-para-um: cada horário pertence a um profissional
    @ManyToOne
    @JoinColumn(name = "profissionalId", referencedColumnName = "idProfissional", nullable = false)
    private ProfissionalEntity profissional;
}
