package br.com.barbeariadopra.controller;

import br.com.barbeariadopra.entity.ServicoEntity;
import br.com.barbeariadopra.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public ResponseEntity<List<ServicoEntity>> listar(
            @RequestParam(required = false) String nome
    ) {
        return ResponseEntity.ok(servicoService.listar(nome));
    }

    @GetMapping("/{idServico}")
    public ResponseEntity<ServicoEntity> buscarPorId(@PathVariable Integer idServico) {
        ServicoEntity servico = servicoService.buscarPorId(idServico);
        if (servico == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(servico);
    }

    @PostMapping
    public ResponseEntity<ServicoEntity> cadastrar(@RequestBody ServicoEntity servico) {
        ServicoEntity novo = servicoService.salvar(servico);
        return ResponseEntity.status(201).body(novo);
    }

    @PutMapping("/{idServico}")
    public ResponseEntity<ServicoEntity> editar(@PathVariable Integer idServico,
                                                @RequestBody ServicoEntity novosDados) {
        ServicoEntity atualizado = servicoService.editar(idServico, novosDados);
        if (atualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{idServico}")
    public ResponseEntity<Void> excluir(@PathVariable Integer idServico) {
        servicoService.excluir(idServico);
        return ResponseEntity.noContent().build();
    }
}
