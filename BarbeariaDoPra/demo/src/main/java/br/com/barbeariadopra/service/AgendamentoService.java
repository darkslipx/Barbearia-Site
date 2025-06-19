package br.com.barbeariadopra.service;

import br.com.barbeariadopra.entity.AgendamentoEntity;
import br.com.barbeariadopra.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final HorariosService horariosService;

    public List<AgendamentoEntity> listarTodos() {
        return agendamentoRepository.findAll();
    }


    public List<AgendamentoEntity> listarPorCliente(Integer clienteId) {
        return agendamentoRepository.findByCliente_IdCliente(clienteId);
    }

    public List<AgendamentoEntity> listarComFiltros(String cliente, String data, String status) {
    List<AgendamentoEntity> lista = listarTodos(); // Pega todos e filtra na mão

    if (cliente != null && !cliente.isEmpty()) {
        lista = lista.stream()
            .filter(a -> a.getCliente() != null
                && a.getCliente().getPessoa() != null
                && a.getCliente().getPessoa().getNome() != null
                && a.getCliente().getPessoa().getNome().toLowerCase().contains(cliente.toLowerCase()))
            .collect(Collectors.toList());
    }

    if (data != null && !data.isEmpty()) {
        lista = lista.stream()
            .filter(a -> a.getData() != null
                && a.getData().toString().equals(data))
            .collect(Collectors.toList());
    }

    if (status != null && !status.isEmpty()) {
        lista = lista.stream()
            .filter(a -> a.getStatus() != null
                && a.getStatus().equalsIgnoreCase(status))
            .collect(Collectors.toList());
    }

    return lista;
}


    public List<AgendamentoEntity> buscarPorProfissionalEData(Integer profissionalId, LocalDate data) {
        return agendamentoRepository.findByProfissional_IdProfissionalAndData(profissionalId, data);
    }

    public AgendamentoEntity buscarPorId(Integer id) {
        return agendamentoRepository.findById(id).orElse(null);
    }

    public AgendamentoEntity incluir(AgendamentoEntity agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    public AgendamentoEntity editar(Integer id, AgendamentoEntity novosDados) {
        AgendamentoEntity existente = agendamentoRepository.findById(id).orElse(null);
        if (existente != null) {
            if (novosDados.getCliente() != null) existente.setCliente(novosDados.getCliente());
            if (novosDados.getProfissional() != null) existente.setProfissional(novosDados.getProfissional());
            if (novosDados.getHorario() != null) existente.setHorario(novosDados.getHorario());
            if (novosDados.getStatus() != null) existente.setStatus(novosDados.getStatus());
            if (novosDados.getServico() != null) existente.setServico(novosDados.getServico());
            if (novosDados.getData() != null) existente.setData(novosDados.getData());

            // Se status RECUSADO -> libera o horário!
            if ("RECUSADO".equalsIgnoreCase(novosDados.getStatus()) && existente.getHorario() != null) {
                horariosService.desbloquearHorario(existente.getHorario().getIdHorario());
            }

            return agendamentoRepository.save(existente);
        }
        return null;
    }

    public void excluir(Integer id) {
        agendamentoRepository.deleteById(id);
    }
}
