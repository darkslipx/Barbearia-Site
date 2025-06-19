package br.com.barbeariadopra.dto;

import lombok.Data;

@Data
public class HorarioStatusDTO {
    private Integer idHorario;
    private String profissionalNome;
    private String diaSemana;
    private String horaInicio;
    private String horaFim;
    private Boolean bloqueado;
    private String statusAgendamento;
}
