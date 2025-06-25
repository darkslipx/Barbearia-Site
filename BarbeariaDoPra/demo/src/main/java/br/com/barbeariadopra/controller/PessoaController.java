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

// Define a classe como um controller REST e injeta automaticamente as dependências
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pessoa")
public class PessoaController {
    // Injeta o serviço responsável pela lógica de pessoas
    private final PessoaService pessoaService;

    // Retorna a lista de todas as pessoas cadastradas
    @GetMapping
    public ResponseEntity<List<PessoaEntity>> listarTodos() {
        List<PessoaEntity> lista = pessoaService.listarTodos();
        return ResponseEntity.ok().body(lista);
    }

    // Busca uma pessoa pelo ID
    @GetMapping("/{idPessoa}")
    public ResponseEntity<PessoaEntity> buscarPorId(@PathVariable int idPessoa) {
        PessoaEntity pessoa = pessoaService.buscarPorId(idPessoa);
        if (pessoa != null) {
            return ResponseEntity.ok(pessoa);
        }
        return ResponseEntity.notFound().build();
    }

    // Cria uma nova pessoa
    @PostMapping
    public ResponseEntity<PessoaEntity> incluir(@RequestBody PessoaEntity pessoa) {
        PessoaEntity novo = pessoaService.incluir(pessoa);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED); // Retorna 201 se criado
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna 400 se não conseguir criar
        }
    }

    // Edita os dados de uma pessoa existente pelo ID
    @PutMapping("/{idPessoa}")
    public ResponseEntity<PessoaEntity> editar(@PathVariable int idPessoa, @RequestBody PessoaEntity pessoa) {
        PessoaEntity atualizado = pessoaService.editar(idPessoa, pessoa);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK); // Retorna 200 se atualizado
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 se não encontrar
        }
    }

    // Altera a senha da pessoa, recebendo o ID e um DTO com senha atual e nova senha
    @PutMapping("/{idPessoa}/senha")
    public ResponseEntity<?> alterarSenha(@PathVariable int idPessoa, @RequestBody TrocaSenhaDTO dto) {
        boolean ok = pessoaService.alterarSenha(idPessoa, dto.getSenhaAtual(), dto.getNovaSenha());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha atual incorreta"); // 401 se senha atual errada
        }
        return ResponseEntity.ok().body("Senha alterada com sucesso"); // 200 se alterar com sucesso
    }

    // Exclui uma pessoa pelo ID
    @DeleteMapping("/{idPessoa}")
    public ResponseEntity<Void> excluir(@PathVariable int idPessoa) {
        pessoaService.excluir(idPessoa);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 sem conteúdo
    }

    // Classe interna para transferência dos dados de troca de senha
    @Getter
    @Setter
    public static class TrocaSenhaDTO {
        private String senhaAtual; // Senha antiga que o usuário já tem
        private String novaSenha;  // Nova senha que o usuário deseja definir
    }
}
