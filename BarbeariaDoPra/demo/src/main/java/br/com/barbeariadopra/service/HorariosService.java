package br.com.barbeariadopra.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.barbeariadopra.dto.HorarioStatusDTO;
import br.com.barbeariadopra.entity.AgendamentoEntity;
import br.com.barbeariadopra.entity.FuncionamentoEntity;
import br.com.barbeariadopra.entity.HorariosEntity;
import br.com.barbeariadopra.entity.ProfissionalEntity;
import br.com.barbeariadopra.enums.DiaSemana;
import br.com.barbeariadopra.repository.AgendamentoRepository;
import br.com.barbeariadopra.repository.FuncionamentoRepository;
import br.com.barbeariadopra.repository.HorariosRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HorariosService {

    

    // INJETA O FUNCIONAMENTO REPOSITORY PARA BUSCAR O FUNCIONAMENTO
    @Autowired
    private final HorariosRepository horariosRepository;
    private final FuncionamentoRepository funcionamentoRepository;
    private final AgendamentoRepository agendamentoRepository;

    public HorariosEntity incluir(HorariosEntity horario) {
        // Garante valor padrão se vier nulo
        if (horario.getBloqueado() == null) {
            horario.setBloqueado(false);
        }
        return horariosRepository.save(horario);
    }

    public HorariosEntity buscarPorId(int idHorario) {
        return horariosRepository.findById(idHorario).orElse(null);
    }

    public List<HorariosEntity> listarTodos() {
        return horariosRepository.findAll();


    }

    public List<HorariosEntity> listarComFiltros(String profissional, String diaSemana, Boolean bloqueado, String horaInicio, String horaFim) {
    List<HorariosEntity> lista = horariosRepository.findAll();

    if (profissional != null && !profissional.isEmpty()) {
        lista = lista.stream()
            .filter(h -> h.getProfissional() != null &&
                h.getProfissional().getPessoa() != null &&
                h.getProfissional().getPessoa().getNome().toLowerCase().contains(profissional.toLowerCase()))
            .collect(Collectors.toList());
    }
    if (diaSemana != null && !diaSemana.isEmpty()) {
        lista = lista.stream()
            .filter(h -> h.getDiaSemana() != null &&
                h.getDiaSemana().toString().equalsIgnoreCase(diaSemana))
            .collect(Collectors.toList());
    }
    if (bloqueado != null) {
        lista = lista.stream()
            .filter(h -> h.getBloqueado() != null && h.getBloqueado().equals(bloqueado))
            .collect(Collectors.toList());
    }
    if (horaInicio != null && !horaInicio.isEmpty()) {
        lista = lista.stream()
            .filter(h -> h.getHoraInicio() != null && h.getHoraInicio().toString().startsWith(horaInicio))
            .collect(Collectors.toList());
    }
    if (horaFim != null && !horaFim.isEmpty()) {
        lista = lista.stream()
            .filter(h -> h.getHoraFim() != null && h.getHoraFim().toString().startsWith(horaFim))
            .collect(Collectors.toList());
    }

    return lista;
}

public List<HorarioStatusDTO> listarHorariosComStatus() {
    List<HorariosEntity> horarios = horariosRepository.findAll();
    List<HorarioStatusDTO> result = new ArrayList<>();

    for (HorariosEntity h : horarios) {
        HorarioStatusDTO dto = new HorarioStatusDTO();
        dto.setIdHorario(h.getIdHorario());
        dto.setProfissionalNome(h.getProfissional().getPessoa().getNome());
        dto.setDiaSemana(h.getDiaSemana().name());
        dto.setHoraInicio(h.getHoraInicio().toString());
        dto.setHoraFim(h.getHoraFim().toString());
        dto.setBloqueado(h.getBloqueado());

        // Busca o agendamento vigente para este horário (status não cancelado/recusado e data >= hoje)
        List<AgendamentoEntity> ags = agendamentoRepository.findAll().stream()
            .filter(a -> a.getHorario().getIdHorario().equals(h.getIdHorario()))
            .filter(a -> !a.getStatus().equalsIgnoreCase("CANCELADO") && !a.getStatus().equalsIgnoreCase("RECUSADO"))
            //.filter(a -> !a.getData().isBefore(LocalDate.now())) // descomente se quiser filtrar apenas futuros/hoje
            .toList();

        if (ags.isEmpty()) {
            dto.setStatusAgendamento("Disponível");
        } else {
            // Exibe o primeiro status (deveria ser só 1 mesmo)
            dto.setStatusAgendamento(ags.get(0).getStatus());
        }

        result.add(dto);
    }
    return result;
}


    public List<HorariosEntity> listarBloqueados() {
        return horariosRepository.findByBloqueado(true);
    }

    public List<HorariosEntity> listarDisponiveis() {
        return horariosRepository.findByBloqueado(false);
    }

    public List<HorariosEntity> listarBloqueadosPorProfissional(Integer idProfissional) {
        return horariosRepository.findByProfissional_IdProfissionalAndBloqueado(idProfissional, true);
    }

    public List<HorariosEntity> listarDisponiveisPorProfissional(Integer idProfissional) {
        return horariosRepository.findByProfissional_IdProfissionalAndBloqueado(idProfissional, false);
    }

    public List<HorariosEntity> horariosDisponiveisParaAgendamento(Integer idProfissional, LocalDate data) {
    return horariosRepository.buscarHorariosDisponiveisParaAgendamento(idProfissional, data);
}



    public HorariosEntity editar(Integer id, HorariosEntity novosDados) {
        HorariosEntity existente = horariosRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setHoraInicio(novosDados.getHoraInicio());
            existente.setHoraFim(novosDados.getHoraFim());
            existente.setProfissional(novosDados.getProfissional());
            existente.setBloqueado(novosDados.getBloqueado() != null ? novosDados.getBloqueado() : false);
            return horariosRepository.save(existente);
        }
        return null;
    }

    public HorariosEntity bloquearHorario(Integer id) {
        HorariosEntity existente = horariosRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setBloqueado(true);
            return horariosRepository.save(existente);
        }
        return null;
    }

    public HorariosEntity desbloquearHorario(Integer id) {
    HorariosEntity existente = horariosRepository.findById(id).orElse(null);
    if (existente != null) {
        existente.setBloqueado(false);
        return horariosRepository.save(existente);
    }
    return null;
}


    public void excluir(Integer idHorario) {
        horariosRepository.deleteById(idHorario);
    }

    // ===========================================
    // NOVO MÉTODO PARA GERAR HORÁRIOS FATIADOS
    // ===========================================
    public int gerarHorariosPorFuncionamento(Integer idFuncionamento, int intervalo) {
    FuncionamentoEntity func = funcionamentoRepository.findById(idFuncionamento)
        .orElseThrow(() -> new IllegalArgumentException("Funcionamento não encontrado"));

    int count = 0;
    LocalTime inicio = func.getHoraInicio();
    LocalTime fim = func.getHoraFim();
    ProfissionalEntity profissional = func.getProfissional();
    DiaSemana diaSemana = func.getDiaSemana();

    // Busca todos os horários já existentes desse profissional/dia da semana
    List<HorariosEntity> horariosDoDia = horariosRepository.findByProfissional_IdProfissionalAndBloqueado(
        profissional.getIdProfissional(), false
    ).stream()
     .filter(h -> h.getDiaSemana() == diaSemana)
     .toList();

    LocalTime atual = inicio;
    while (!atual.isAfter(fim.minusMinutes(intervalo))) {
        final LocalTime horaInicio = atual;
        final LocalTime horaFim = atual.plusMinutes(intervalo);

        boolean exists = horariosDoDia.stream()
            .anyMatch(h ->
                h.getHoraInicio().equals(horaInicio) &&
                h.getHoraFim().equals(horaFim)
            );

        if (!exists) {
            HorariosEntity novo = new HorariosEntity();
            novo.setBloqueado(false);
            novo.setDiaSemana(diaSemana);
            novo.setHoraInicio(horaInicio);
            novo.setHoraFim(horaFim);
            novo.setProfissional(profissional);
            horariosRepository.save(novo);
            count++;
        }
        atual = atual.plusMinutes(intervalo);
    }
    return count;
}
}