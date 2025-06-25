package br.com.barbeariadopra.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.barbeariadopra.entity.AgendamentoEntity;
import br.com.barbeariadopra.entity.FuncionamentoEntity;
import br.com.barbeariadopra.entity.HorariosEntity;
import br.com.barbeariadopra.entity.ClienteEntity;
import br.com.barbeariadopra.entity.ProfissionalEntity;
import br.com.barbeariadopra.entity.ServicoEntity;
import br.com.barbeariadopra.service.AgendamentoService;
import br.com.barbeariadopra.service.FuncionamentoService;
import br.com.barbeariadopra.service.HorariosService;
import br.com.barbeariadopra.service.ServicoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agendamentos")
public class AgendamentoController {

    // --- Injeção de serviços necessários ---
    private final AgendamentoService agendamentoService;
    private final FuncionamentoService funcionamentoService;
    private final HorariosService horariosService;
    private final ServicoService servicoService;

    // ======================== LISTAR AGENDAMENTOS COM FILTRO ==========================
    @GetMapping
    public ResponseEntity<List<AgendamentoEntity>> listarComFiltros(
        @RequestParam(required = false) String cliente,
        @RequestParam(required = false) String data,
        @RequestParam(required = false) String status
    ) {
        // Busca agendamentos com filtros opcionais: cliente, data, status
        List<AgendamentoEntity> lista = agendamentoService.listarComFiltros(cliente, data, status);
        return ResponseEntity.ok(lista);
    }

    // ======================== BUSCAR AGENDAMENTO POR ID ==========================
    @GetMapping("/{idAgendamento}")
    public ResponseEntity<AgendamentoEntity> buscarPorId(@PathVariable int idAgendamento) {
        AgendamentoEntity agendamento = agendamentoService.buscarPorId(idAgendamento);
        if (agendamento != null) return ResponseEntity.ok(agendamento);
        return ResponseEntity.notFound().build();
    }

    // ======================== INCLUIR NOVO AGENDAMENTO ==========================
    @PostMapping
    public ResponseEntity<AgendamentoEntity> incluir(@RequestBody Map<String, Object> payload) {
        try {
            // --- Extrai e trata IDs dos objetos aninhados recebidos do front-end ---
            Object clienteIdObj = ((Map) payload.get("cliente")).get("idCliente");
            Integer clienteId = clienteIdObj instanceof Integer ? (Integer) clienteIdObj : Integer.valueOf(clienteIdObj.toString());
            Integer profissionalId = ((Map<String, Integer>) payload.get("profissional")).get("idProfissional");
            Integer horarioId = ((Map<String, Integer>) payload.get("horario")).get("idHorario");
            String status = (String) payload.get("status");
            String dataStr = (String) payload.get("data");

            // --- Serviço pode vir de diferentes formas ---
            Object servicoObj = payload.get("servico");
            Integer servicoId = null;
            if (servicoObj instanceof Map) {
                Object idServicoObj = ((Map) servicoObj).get("idServico");
                if (idServicoObj != null) {
                    servicoId = idServicoObj instanceof Integer ? (Integer) idServicoObj : Integer.valueOf(idServicoObj.toString());
                }
            }
            LocalDate data = LocalDate.parse(dataStr);

            // Busca as entidades pelos seus IDs
            ClienteEntity cliente = new ClienteEntity();
            cliente.setIdCliente(clienteId);
            ProfissionalEntity profissional = new ProfissionalEntity();
            profissional.setIdProfissional(profissionalId);
            HorariosEntity horario = horariosService.buscarPorId(horarioId);
            ServicoEntity servico = servicoService.buscarPorId(servicoId);

            // Não permite agendamento para serviço inexistente
            if (servico == null) return ResponseEntity.badRequest().build();

            // Só permite agendar se o horário está realmente livre!
            if (horario == null || horario.getBloqueado()) {
                return ResponseEntity.badRequest().build();
            }
            // Verifica se já existe agendamento nesse horário/data
            List<AgendamentoEntity> ags = agendamentoService.buscarPorProfissionalEData(profissionalId, data);
            boolean jaAgendado = ags.stream()
                .filter(a -> !a.getStatus().equalsIgnoreCase("CANCELADO") && !a.getStatus().equalsIgnoreCase("RECUSADO"))
                .anyMatch(a -> a.getHorario().getIdHorario().equals(horarioId));

            if (jaAgendado) {
                return ResponseEntity.badRequest().build();
            }

            // Cria objeto de agendamento e salva
            AgendamentoEntity agendamento = new AgendamentoEntity();
            agendamento.setCliente(cliente);
            agendamento.setProfissional(profissional);
            agendamento.setHorario(horario);
            agendamento.setStatus(status);
            agendamento.setServico(servico); // <-- agora é o objeto!
            agendamento.setData(data);

            AgendamentoEntity novo = agendamentoService.incluir(agendamento);
            return ResponseEntity.status(201).body(novo);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // ======================== CANCELAR AGENDAMENTO ==========================
    @PutMapping("/{idAgendamento}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable int idAgendamento) {
        AgendamentoEntity agendamento = agendamentoService.buscarPorId(idAgendamento);
        if (agendamento == null) return ResponseEntity.notFound().build();

        // Só permite cancelar se for PENDENTE ou CONFIRMADO
        String status = agendamento.getStatus();
        if (!"PENDENTE".equalsIgnoreCase(status) && !"CONFIRMADO".equalsIgnoreCase(status)) {
            return ResponseEntity.status(409).body("Só é possível cancelar agendamentos pendentes ou confirmados.");
        }
        agendamento.setStatus("CANCELADO");
        agendamentoService.incluir(agendamento);

        // --- Desbloqueia o horário associado ---
        if (agendamento.getHorario() != null) {
            HorariosEntity horario = agendamento.getHorario();
            horario.setBloqueado(false);
            horariosService.incluir(horario);
        }

        return ResponseEntity.ok().build();
    }

    // ======================== EDITAR AGENDAMENTO ==========================
    @PutMapping("/{idAgendamento}")
    public ResponseEntity<AgendamentoEntity> editar(@PathVariable int idAgendamento, @RequestBody AgendamentoEntity agendamento) {
        AgendamentoEntity atualizado = agendamentoService.editar(idAgendamento, agendamento);
        if (atualizado != null) return ResponseEntity.ok(atualizado);
        return ResponseEntity.notFound().build();
    }

    // ======================== EXCLUIR AGENDAMENTO ==========================
    @DeleteMapping("/{idAgendamento}")
    public ResponseEntity<Void> excluir(@PathVariable int idAgendamento) {
        agendamentoService.excluir(idAgendamento);
        return ResponseEntity.noContent().build();
    }

    // ======================== LISTAR AGENDAMENTOS POR CLIENTE ==========================
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<AgendamentoEntity>> listarPorCliente(@PathVariable Integer idCliente) {
        List<AgendamentoEntity> agendamentos = agendamentoService.listarPorCliente(idCliente);
        return ResponseEntity.ok(agendamentos);
    }

    // ======================== NOVO ENDPOINT: LISTA DE HORÁRIOS DISPONÍVEIS ==========================
    @GetMapping("/horarios-disponiveis")
    public ResponseEntity<List<Map<String, Object>>> listarHorariosDisponiveis(
        @RequestParam Integer profissionalId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        DayOfWeek dayOfWeek = data.getDayOfWeek();
        Optional<FuncionamentoEntity> funcionamentoOpt = funcionamentoService
            .buscarPorProfissionalEDiaSemana(profissionalId, dayOfWeek);

        if (funcionamentoOpt.isEmpty()) {
            // Nenhuma disponibilidade cadastrada para esse profissional/dia
            return ResponseEntity.ok(Collections.emptyList());
        }
        FuncionamentoEntity funcionamento = funcionamentoOpt.get();

        // 1. Busca horários livres daquele profissional no dia da semana informado
        List<HorariosEntity> horariosDisponiveis = horariosService
            .listarDisponiveisPorProfissional(profissionalId)
            .stream()
            .filter(h -> h.getDiaSemana().name().equalsIgnoreCase(dayOfWeekToEnum(dayOfWeek)))
            .filter(h -> h.getHoraInicio().compareTo(funcionamento.getHoraInicio()) >= 0)
            .filter(h -> h.getHoraFim().compareTo(funcionamento.getHoraFim()) <= 0)
            .collect(Collectors.toList());

        // 2. Busca horários ocupados naquele dia (pendentes e confirmados)
        Set<Integer> idsOcupados = agendamentoService.buscarPorProfissionalEData(profissionalId, data)
            .stream()
            .filter(a -> !a.getStatus().equalsIgnoreCase("CANCELADO") 
                      && !a.getStatus().equalsIgnoreCase("RECUSADO"))
            .map(a -> a.getHorario().getIdHorario())
            .collect(Collectors.toSet());

        // 3. Filtra horários disponíveis, removendo os já ocupados
        List<Map<String, Object>> response = horariosDisponiveis.stream()
            .filter(h -> !idsOcupados.contains(h.getIdHorario()))
            .map(h -> {
                Map<String, Object> m = new HashMap<>();
                m.put("idHorario", h.getIdHorario());
                m.put("horaInicio", h.getHoraInicio().toString().substring(0, 5));
                return m;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ======================== UTILITÁRIO: TRADUZ DayOfWeek DO JAVA PARA O ENUM USADO NO SISTEMA ==========================
    private String dayOfWeekToEnum(DayOfWeek dayOfWeek) {
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
