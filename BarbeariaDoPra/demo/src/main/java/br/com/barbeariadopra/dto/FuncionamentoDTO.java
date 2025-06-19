package br.com.barbeariadopra.dto;

import br.com.barbeariadopra.enums.DiaSemana;
import lombok.Data;

@Data
public class FuncionamentoDTO {
    private DiaSemana diaSemana;           // Aceita string tipo "Segunda-feira"
    private Integer profissionalId;
    private String horaInicio;
    private String horaFim;
    private String horarioDisponivel;  

}
