package br.com.barbeariadopra.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbagendamentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AgendamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAgendamento", nullable = false)
    private Integer idAgendamento;

    @ManyToOne
    @JoinColumn(name = "clienteId", referencedColumnName = "idCliente",  nullable = false)
    private ClienteEntity cliente;

    @ManyToOne
    @JoinColumn(name = "profissionalId", referencedColumnName = "idProfissional", nullable = false)
    private ProfissionalEntity profissional;

    @ManyToOne
    @JoinColumn(name = "horarioId", referencedColumnName = "idHorario", nullable = false)
    private HorariosEntity horario;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "servico_id")
    private ServicoEntity servico;

    @Column(name = "data", nullable = false)
    private LocalDate data;
}
