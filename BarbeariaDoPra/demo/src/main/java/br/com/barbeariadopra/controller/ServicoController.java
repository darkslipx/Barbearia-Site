package br.com.barbeariadopra.controller;

import br.com.barbeariadopra.entity.ServicoEntity;
import br.com.barbeariadopra.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Define este controller como um REST controller do Spring
@RestController
@RequiredArgsConstructor // Injeta dependências pelo construtor, dispensando o uso de @Autowired
@RequestMapping("/servicos") // Define o caminho base dos endpoints como /servicos
public class ServicoController {

    // Injeta o serviço responsável pela lógica dos serviços
    private final ServicoService servicoService;

    // Lista todos os serviços ou filtra pelo nome (se informado na query)
    @GetMapping
    public ResponseEntity<List<ServicoEntity>> listar(
            @RequestParam(required = false) String nome
    ) {
        return ResponseEntity.ok(servicoService.listar(nome));
    }

    // Busca um serviço específico pelo ID
    @GetMapping("/{idServico}")
    public ResponseEntity<ServicoEntity> buscarPorId(@PathVariable Integer idServico) {
        ServicoEntity servico = servicoService.buscarPorId(idServico);
        if (servico == null) return ResponseEntity.notFound().build(); // 404 se não encontrar
        return ResponseEntity.ok(servico); // 200 e retorna o serviço encontrado
    }

    // Cadastra um novo serviço
    @PostMapping
    public ResponseEntity<ServicoEntity> cadastrar(@RequestBody ServicoEntity servico) {
        ServicoEntity novo = servicoService.salvar(servico);
        return ResponseEntity.status(201).body(novo); // 201 CREATED se sucesso
    }

    // Edita um serviço existente pelo ID
    @PutMapping("/{idServico}")
    public ResponseEntity<ServicoEntity> editar(@PathVariable Integer idServico,
                                                @RequestBody ServicoEntity novosDados) {
        ServicoEntity atualizado = servicoService.editar(idServico, novosDados);
        if (atualizado == null) return ResponseEntity.notFound().build(); // 404 se não existe
        return ResponseEntity.ok(atualizado); // 200 com serviço atualizado
    }

    // Remove um serviço pelo ID
    @DeleteMapping("/{idServico}")
    public ResponseEntity<Void> excluir(@PathVariable Integer idServico) {
        servicoService.excluir(idServico);
        return ResponseEntity.noContent().build(); // 204 NO CONTENT após exclusão
    }
}
