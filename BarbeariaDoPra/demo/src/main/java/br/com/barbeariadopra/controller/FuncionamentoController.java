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

// Define a classe como um controller REST, para receber requisições HTTP
@RestController
@RequiredArgsConstructor // Gera construtor com os campos finais (injeção de dependência)
@RequestMapping("/funcionamento") // Define o prefixo dos endpoints deste controller
public class FuncionamentoController {
    // Injeta as dependências dos services
    private final FuncionamentoService funcionamentoService;
    private final HorariosService horariosService; // Para geração automática de horários

    // ==================== LISTA TODOS OS FUNCIONAMENTOS ====================
    @GetMapping
    public ResponseEntity<List<FuncionamentoEntity>> listarTodos() {
        // Retorna todos os registros da tabela funcionamento
        return ResponseEntity.ok(funcionamentoService.listarTodos());
    }

    // ==================== BUSCA UM FUNCIONAMENTO POR ID ====================
    @GetMapping("/{idFuncionamento}")
    public ResponseEntity<FuncionamentoEntity> buscarPorId(@PathVariable int idFuncionamento) {
        FuncionamentoEntity funcionamento = funcionamentoService.buscarPorId(idFuncionamento);
        if (funcionamento != null) {
            return ResponseEntity.ok(funcionamento); // Sucesso, retorna o funcionamento encontrado
        }
        return ResponseEntity.notFound().build(); // Se não encontrar, retorna 404
    }

    // ====== LISTA OS DIAS DA SEMANA QUE UM PROFISSIONAL TEM FUNCIONAMENTO CADASTRADO ======
    @GetMapping("/profissional/{idProfissional}/dias-disponiveis")
    public ResponseEntity<List<String>> diasDisponiveisPorProfissional(@PathVariable Integer idProfissional) {
        // Busca uma lista de enums DiaSemana, converte para string e retorna
        List<DiaSemana> dias = funcionamentoService.diasDisponiveisPorProfissional(idProfissional);
        List<String> nomes = dias.stream().map(DiaSemana::name).collect(Collectors.toList());
        return ResponseEntity.ok(nomes);
    }

    // ====== LISTA TODOS OS FUNCIONAMENTOS DE UM PROFISSIONAL EM UM DETERMINADO DIA DA SEMANA ======
    @GetMapping("/profissional/{idProfissional}/dia/{diaSemana}/horarios-disponiveis")
    public ResponseEntity<List<FuncionamentoEntity>> horariosDisponiveisPorDia(
            @PathVariable Integer idProfissional,
            @PathVariable String diaSemana
    ) {
        // Converte string para enum e retorna os funcionamentos do profissional para aquele dia
        DiaSemana enumDia = DiaSemana.valueOf(diaSemana.toUpperCase());
        List<FuncionamentoEntity> horarios = funcionamentoService.horariosDisponiveisPorProfissionalEDia(idProfissional, enumDia);
        return ResponseEntity.ok(horarios);
    }

    // ==================== CADASTRA UM NOVO FUNCIONAMENTO ====================
    @PostMapping
    public ResponseEntity<FuncionamentoEntity> incluir(@RequestBody FuncionamentoEntity funcionamento) {
        FuncionamentoEntity novo = funcionamentoService.incluir(funcionamento);
        if (novo != null) {
            // Sucesso: retorna 201 CREATED e o funcionamento cadastrado
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            // Falha: retorna 400 BAD REQUEST
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== ATUALIZA UM FUNCIONAMENTO EXISTENTE ====================
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

    // ==================== EXCLUI UM FUNCIONAMENTO ====================
    @DeleteMapping("/{idFuncionamento}")
    public ResponseEntity<Void> excluir(@PathVariable int idFuncionamento) {
        funcionamentoService.excluir(idFuncionamento);
        // Retorna 204 NO CONTENT indicando sucesso sem retorno no corpo
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // =========== GERA AUTOMATICAMENTE OS HORÁRIOS BASEADO NO FUNCIONAMENTO ===========
    @PostMapping("/{idFuncionamento}/gerar-horarios")
    public ResponseEntity<?> gerarHorariosFuncionamento(
            @PathVariable Integer idFuncionamento,
            @RequestParam(defaultValue = "30") Integer intervalo // 30 minutos por padrão, pode mudar via parâmetro
    ) {
        try {
            // Chama o service para gerar horários de acordo com o funcionamento e intervalo
            int qtd = horariosService.gerarHorariosPorFuncionamento(idFuncionamento, intervalo);
            return ResponseEntity.ok("Horários gerados com sucesso: " + qtd);
        } catch (Exception e) {
            // Caso aconteça algum erro, retorna 500 com a mensagem do erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar horários: " + e.getMessage());
        }
    }
}
