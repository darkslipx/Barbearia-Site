package br.com.barbeariadopra.service;

import br.com.barbeariadopra.entity.AgendamentoEntity;
import br.com.barbeariadopra.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Classe de serviço responsável pela lógica de negócios dos agendamentos
@Service
@RequiredArgsConstructor // Injeta as dependências via construtor
public class AgendamentoService {

    // Repositório de acesso ao banco de agendamentos
    private final AgendamentoRepository agendamentoRepository;
    // Serviço de horários para manipulação de horários ao editar/cancelar agendamento
    private final HorariosService horariosService;

    // Lista todos os agendamentos do sistema
    public List<AgendamentoEntity> listarTodos() {
        return agendamentoRepository.findAll();
    }

    // Lista agendamentos filtrando por cliente (pelo ID do cliente)
    public List<AgendamentoEntity> listarPorCliente(Integer clienteId) {
        return agendamentoRepository.findByCliente_IdCliente(clienteId);
    }

    // Lista agendamentos aplicando filtros manuais por nome do cliente, data e status
    public List<AgendamentoEntity> listarComFiltros(String cliente, String data, String status) {
        List<AgendamentoEntity> lista = listarTodos(); // Busca todos e filtra em memória

        // Filtra por nome do cliente, se informado
        if (cliente != null && !cliente.isEmpty()) {
            lista = lista.stream()
                .filter(a -> a.getCliente() != null
                    && a.getCliente().getPessoa() != null
                    && a.getCliente().getPessoa().getNome() != null
                    && a.getCliente().getPessoa().getNome().toLowerCase().contains(cliente.toLowerCase()))
                .collect(Collectors.toList());
        }

        // Filtra por data, se informada
        if (data != null && !data.isEmpty()) {
            lista = lista.stream()
                .filter(a -> a.getData() != null
                    && a.getData().toString().equals(data))
                .collect(Collectors.toList());
        }

        // Filtra por status, se informado
        if (status != null && !status.isEmpty()) {
            lista = lista.stream()
                .filter(a -> a.getStatus() != null
                    && a.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        }

        return lista;
    }

    // Busca agendamentos de um profissional em uma data específica
    public List<AgendamentoEntity> buscarPorProfissionalEData(Integer profissionalId, LocalDate data) {
        return agendamentoRepository.findByProfissional_IdProfissionalAndData(profissionalId, data);
    }

    // Busca um agendamento pelo ID
    public AgendamentoEntity buscarPorId(Integer id) {
        return agendamentoRepository.findById(id).orElse(null);
    }

    // Salva um novo agendamento no banco
    public AgendamentoEntity incluir(AgendamentoEntity agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    // Edita os dados de um agendamento existente, atualizando apenas os campos informados
    public AgendamentoEntity editar(Integer id, AgendamentoEntity novosDados) {
        AgendamentoEntity existente = agendamentoRepository.findById(id).orElse(null);
        if (existente != null) {
            if (novosDados.getCliente() != null) existente.setCliente(novosDados.getCliente());
            if (novosDados.getProfissional() != null) existente.setProfissional(novosDados.getProfissional());
            if (novosDados.getHorario() != null) existente.setHorario(novosDados.getHorario());
            if (novosDados.getStatus() != null) existente.setStatus(novosDados.getStatus());
            if (novosDados.getServico() != null) existente.setServico(novosDados.getServico());
            if (novosDados.getData() != null) existente.setData(novosDados.getData());

            // Se o status for alterado para "RECUSADO", libera o horário (desbloqueia)
            if ("RECUSADO".equalsIgnoreCase(novosDados.getStatus()) && existente.getHorario() != null) {
                horariosService.desbloquearHorario(existente.getHorario().getIdHorario());
            }

            return agendamentoRepository.save(existente);
        }
        return null;
    }

    // Exclui um agendamento pelo ID
    public void excluir(Integer id) {
        agendamentoRepository.deleteById(id);
    }
}
