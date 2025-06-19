package br.com.barbeariadopra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.barbeariadopra.entity.PessoaEntity;
import br.com.barbeariadopra.service.PessoaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pessoa")
public class PessoaController {
    private final PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<PessoaEntity>> listarTodos() {
        List<PessoaEntity> lista = pessoaService.listarTodos();
        return ResponseEntity.ok().body(lista);
    }

    @GetMapping("/{idPessoa}")
    public ResponseEntity<PessoaEntity> buscarPorId(@PathVariable int idPessoa) {
        PessoaEntity pessoa = pessoaService.buscarPorId(idPessoa);
        if (pessoa != null) {
            return ResponseEntity.ok(pessoa);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PessoaEntity> incluir(@RequestBody PessoaEntity pessoa) {
        PessoaEntity novo = pessoaService.incluir(pessoa);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idPessoa}")
    public ResponseEntity<PessoaEntity> editar(@PathVariable int idPessoa, @RequestBody PessoaEntity pessoa) {
        PessoaEntity atualizado = pessoaService.editar(idPessoa, pessoa);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{idPessoa}/senha")
    public ResponseEntity<?> alterarSenha(@PathVariable int idPessoa, @RequestBody TrocaSenhaDTO dto) {
        boolean ok = pessoaService.alterarSenha(idPessoa, dto.getSenhaAtual(), dto.getNovaSenha());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha atual incorreta");
        }
        return ResponseEntity.ok().body("Senha alterada com sucesso");
    }

    @DeleteMapping("/{idPessoa}")
    public ResponseEntity<Void> excluir(@PathVariable int idPessoa) {
        pessoaService.excluir(idPessoa);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Getter
    @Setter
    public static class TrocaSenhaDTO {
        private String senhaAtual;
        private String novaSenha;
    }
}
