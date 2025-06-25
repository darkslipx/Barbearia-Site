package br.com.barbeariadopra.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

// Entidade que representa um agendamento no sistema
@Entity
@Table(name = "tbagendamentos") // Define o nome da tabela no banco de dados
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok para getters/setters e construtores
public class AgendamentoEntity {

    // Identificador único do agendamento (PK, autoincremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAgendamento", nullable = false)
    private Integer idAgendamento;

    // Relação muitos-para-um: um agendamento tem um cliente
    @ManyToOne
    @JoinColumn(name = "clienteId", referencedColumnName = "idCliente", nullable = false)
    private ClienteEntity cliente;

    // Relação muitos-para-um: um agendamento tem um profissional
    @ManyToOne
    @JoinColumn(name = "profissionalId", referencedColumnName = "idProfissional", nullable = false)
    private ProfissionalEntity profissional;

    // Relação muitos-para-um: um agendamento tem um horário específico
    @ManyToOne
    @JoinColumn(name = "horarioId", referencedColumnName = "idHorario", nullable = false)
    private HorariosEntity horario;

    // Status do agendamento (ex: PENDENTE, CONFIRMADO, CANCELADO)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    // Relação muitos-para-um: um agendamento está associado a um serviço
    @ManyToOne
    @JoinColumn(name = "servico_id")
    private ServicoEntity servico;

    // Data do agendamento
    @Column(name = "data", nullable = false)
    private LocalDate data;
}
