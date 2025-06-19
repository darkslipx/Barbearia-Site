package br.com.barbeariadopra.service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.barbeariadopra.entity.FuncionamentoEntity;
import br.com.barbeariadopra.enums.DiaSemana;
import br.com.barbeariadopra.repository.FuncionamentoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncionamentoService {

    private final FuncionamentoRepository funcionamentoRepository;

    public FuncionamentoEntity incluir(FuncionamentoEntity funcionamento) {
        // Preencher o campo obrigatório caso venha vazio
        if (funcionamento.getHorarioDisponivel() == null || funcionamento.getHorarioDisponivel().isEmpty()) {
            funcionamento.setHorarioDisponivel(
                funcionamento.getHoraInicio() + " - " + funcionamento.getHoraFim()
            );
        }
        return funcionamentoRepository.save(funcionamento);
    }

    public FuncionamentoEntity buscarPorId(int idFuncionamento) {
        return funcionamentoRepository.findById(idFuncionamento).orElse(null);
    }

    public FuncionamentoEntity editar(Integer id, FuncionamentoEntity novosDados) {
        FuncionamentoEntity existente = funcionamentoRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setDiaSemana(novosDados.getDiaSemana());
            existente.setProfissional(novosDados.getProfissional());
            existente.setHoraInicio(novosDados.getHoraInicio());
            existente.setHoraFim(novosDados.getHoraFim());
            if (novosDados.getHorarioDisponivel() == null || novosDados.getHorarioDisponivel().isEmpty()) {
                existente.setHorarioDisponivel(
                    novosDados.getHoraInicio() + " - " + novosDados.getHoraFim()
                );
            } else {
                existente.setHorarioDisponivel(novosDados.getHorarioDisponivel());
            }
            return funcionamentoRepository.save(existente);
        }
        return null;
    }

    public List<FuncionamentoEntity> listarTodos() {
        return funcionamentoRepository.findAll();
    }

    public void excluir(Integer idFuncionamento) {
        funcionamentoRepository.deleteById(idFuncionamento);
    }

    // Busca TODOS os dias de funcionamento de um profissional
    public List<DiaSemana> diasDisponiveisPorProfissional(Integer idProfissional) {
        return funcionamentoRepository.findByProfissional_IdProfissional(idProfissional)
                .stream()
                .map(FuncionamentoEntity::getDiaSemana)
                .distinct()
                .collect(Collectors.toList());
    }

    // Busca todos os funcionamentos (faixas de horários) para o profissional num dia específico
    public List<FuncionamentoEntity> horariosDisponiveisPorProfissionalEDia(Integer idProfissional, DiaSemana diaSemana) {
        return funcionamentoRepository.findByProfissional_IdProfissionalAndDiaSemana(idProfissional, diaSemana);
    }

    // ==============================
    // MÉTODO PARA O FLUXO DO AGENDAMENTO
    // ==============================
    // Busca funcionamento do profissional para o dia da semana (usado pelo endpoint do agendamento)
    public Optional<FuncionamentoEntity> buscarPorProfissionalEDiaSemana(Integer profissionalId, DayOfWeek dayOfWeek) {
        String enumDia = diaSemanaToEnum(dayOfWeek);
        return funcionamentoRepository.findByProfissional_IdProfissional(profissionalId)
                .stream()
                .filter(f -> f.getDiaSemana().name().equalsIgnoreCase(enumDia))
                .findFirst();
    }

    // Traduz DayOfWeek do Java para seu Enum de DiaSemana
    private String diaSemanaToEnum(DayOfWeek dayOfWeek) {
        switch(dayOfWeek) {
            case MONDAY:    return "SEGUNDA_FEIRA";
            case TUESDAY:   return "TERCA_FEIRA";
            case WEDNESDAY: return "QUARTA_FEIRA";
            case THURSDAY:  return "QUINTA_FEIRA";
            case FRIDAY:    return "SEXTA_FEIRA";
            case SATURDAY:  return "SABADO";
            case SUNDAY:    return "DOMINGO";
            default:        return "";
        }
    }
}
