package br.com.barbeariadopra.dto;

import br.com.barbeariadopra.enums.DiaSemana;
import lombok.Data;

// DTO utilizado para transferir os dados do funcionamento de um profissional
@Data
public class FuncionamentoDTO {
    private DiaSemana diaSemana;         // Dia da semana (enum), aceita valores tipo "Segunda-feira"
    private Integer profissionalId;      // ID do profissional relacionado ao funcionamento
    private String horaInicio;           // Hora de início do expediente (formato string, ex: "08:00")
    private String horaFim;              // Hora de fim do expediente (formato string, ex: "18:00")
    private String horarioDisponivel;    // Campo adicional para indicar horário disponível, se necessário
}
