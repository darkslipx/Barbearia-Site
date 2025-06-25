package br.com.barbeariadopra.dto;

import lombok.Data;

// DTO que representa o status de um horário, usado para exibição de dados detalhados sobre horários de agendamento
@Data 
public class HorarioStatusDTO {
    private Integer idHorario;            // Identificador único do horário
    private String profissionalNome;      // Nome do profissional relacionado ao horário
    private String diaSemana;             // Dia da semana do horário (ex: "Segunda-feira")
    private String horaInicio;            // Hora de início do horário (formato string, ex: "09:00")
    private String horaFim;               // Hora de fim do horário (formato string, ex: "10:00")
    private Boolean bloqueado;            // Indica se o horário está bloqueado (true/false)
    private String statusAgendamento;     // Status atual do agendamento (ex: "Disponível", "Agendado", etc.)
}
