package br.com.barbeariadopra.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.barbeariadopra.entity.FuncionamentoEntity;
import br.com.barbeariadopra.enums.DiaSemana;
import br.com.barbeariadopra.service.FuncionamentoService;
import br.com.barbeariadopra.service.HorariosService; // ADICIONADO
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funcionamento")
public class FuncionamentoController {
    private final FuncionamentoService funcionamentoService;
    private final HorariosService horariosService; // INJETADO

    @GetMapping
    public ResponseEntity<List<FuncionamentoEntity>> listarTodos() {
        return ResponseEntity.ok(funcionamentoService.listarTodos());
    }

    @GetMapping("/{idFuncionamento}")
    public ResponseEntity<FuncionamentoEntity> buscarPorId(@PathVariable int idFuncionamento) {
        FuncionamentoEntity funcionamento = funcionamentoService.buscarPorId(idFuncionamento);
        if (funcionamento != null) {
            return ResponseEntity.ok(funcionamento);
        }
        return ResponseEntity.notFound().build();
    }

    // Retorna os dias disponíveis para o profissional (Enum DiaSemana)
    @GetMapping("/profissional/{idProfissional}/dias-disponiveis")
    public ResponseEntity<List<String>> diasDisponiveisPorProfissional(@PathVariable Integer idProfissional) {
        List<DiaSemana> dias = funcionamentoService.diasDisponiveisPorProfissional(idProfissional);
        List<String> nomes = dias.stream().map(DiaSemana::name).collect(Collectors.toList());
        return ResponseEntity.ok(nomes);
    }

    // Retorna todos os funcionamentos do profissional em um dia específico
    @GetMapping("/profissional/{idProfissional}/dia/{diaSemana}/horarios-disponiveis")
    public ResponseEntity<List<FuncionamentoEntity>> horariosDisponiveisPorDia(
            @PathVariable Integer idProfissional,
            @PathVariable String diaSemana
    ) {
        DiaSemana enumDia = DiaSemana.valueOf(diaSemana.toUpperCase());
        List<FuncionamentoEntity> horarios = funcionamentoService.horariosDisponiveisPorProfissionalEDia(idProfissional, enumDia);
        return ResponseEntity.ok(horarios);
    }

    @PostMapping
    public ResponseEntity<FuncionamentoEntity> incluir(@RequestBody FuncionamentoEntity funcionamento) {
        FuncionamentoEntity novo = funcionamentoService.incluir(funcionamento);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idFuncionamento}")
    public ResponseEntity<FuncionamentoEntity> editar(
            @PathVariable int idFuncionamento,
            @RequestBody FuncionamentoEntity funcionamento) {
        FuncionamentoEntity atualizado = funcionamentoService.editar(idFuncionamento, funcionamento);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{idFuncionamento}")
    public ResponseEntity<Void> excluir(@PathVariable int idFuncionamento) {
        funcionamentoService.excluir(idFuncionamento);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // =============== NOVO ENDPOINT ===============
    @PostMapping("/{idFuncionamento}/gerar-horarios")
    public ResponseEntity<?> gerarHorariosFuncionamento(
            @PathVariable Integer idFuncionamento,
            @RequestParam(defaultValue = "30") Integer intervalo // 30 minutos por padrão, pode mudar
    ) {
        try {
            int qtd = horariosService.gerarHorariosPorFuncionamento(idFuncionamento, intervalo);
            return ResponseEntity.ok("Horários gerados com sucesso: " + qtd);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar horários: " + e.getMessage());
        }
    }
}
