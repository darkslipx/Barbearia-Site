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

// Serviço responsável pela lógica de negócios relacionada ao funcionamento dos profissionais
@Service
@RequiredArgsConstructor
public class FuncionamentoService {

    // Repositório de acesso ao banco de dados para funcionamento
    private final FuncionamentoRepository funcionamentoRepository;

    // Salva um novo funcionamento. Preenche o campo 'horarioDisponivel' caso venha vazio.
    public FuncionamentoEntity incluir(FuncionamentoEntity funcionamento) {
        // Preencher o campo obrigatório caso venha vazio
        if (funcionamento.getHorarioDisponivel() == null || funcionamento.getHorarioDisponivel().isEmpty()) {
            funcionamento.setHorarioDisponivel(
                funcionamento.getHoraInicio() + " - " + funcionamento.getHoraFim()
            );
        }
        return funcionamentoRepository.save(funcionamento);
    }

    // Busca funcionamento pelo ID
    public FuncionamentoEntity buscarPorId(int idFuncionamento) {
        return funcionamentoRepository.findById(idFuncionamento).orElse(null);
    }

    // Edita um funcionamento já existente, atualizando todos os campos
    public FuncionamentoEntity editar(Integer id, FuncionamentoEntity novosDados) {
        FuncionamentoEntity existente = funcionamentoRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setDiaSemana(novosDados.getDiaSemana());
            existente.setProfissional(novosDados.getProfissional());
            existente.setHoraInicio(novosDados.getHoraInicio());
            existente.setHoraFim(novosDados.getHoraFim());
            // Atualiza ou calcula o campo 'horarioDisponivel'
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

    // Lista todos os funcionamentos cadastrados
    public List<FuncionamentoEntity> listarTodos() {
        return funcionamentoRepository.findAll();
    }

    // Exclui um funcionamento pelo ID
    public void excluir(Integer idFuncionamento) {
        funcionamentoRepository.deleteById(idFuncionamento);
    }

    // Busca todos os dias da semana em que um profissional está disponível
    public List<DiaSemana> diasDisponiveisPorProfissional(Integer idProfissional) {
        return funcionamentoRepository.findByProfissional_IdProfissional(idProfissional)
                .stream()
                .map(FuncionamentoEntity::getDiaSemana)
                .distinct()
                .collect(Collectors.toList());
    }

    // Busca todos os funcionamentos (faixas de horários) para um profissional num dia da semana específico
    public List<FuncionamentoEntity> horariosDisponiveisPorProfissionalEDia(Integer idProfissional, DiaSemana diaSemana) {
        return funcionamentoRepository.findByProfissional_IdProfissionalAndDiaSemana(idProfissional, diaSemana);
    }

    // ==============================
    // MÉTODO PARA O FLUXO DO AGENDAMENTO
    // ==============================

    // Busca funcionamento de um profissional para determinado dia da semana (usado pelo endpoint de agendamento)
    public Optional<FuncionamentoEntity> buscarPorProfissionalEDiaSemana(Integer profissionalId, DayOfWeek dayOfWeek) {
        String enumDia = diaSemanaToEnum(dayOfWeek);
        return funcionamentoRepository.findByProfissional_IdProfissional(profissionalId)
                .stream()
                .filter(f -> f.getDiaSemana().name().equalsIgnoreCase(enumDia))
                .findFirst();
    }

    // Converte DayOfWeek do Java para o enum DiaSemana utilizado no sistema
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
