package br.com.barbeariadopra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.barbeariadopra.entity.ProfissionalEntity;
import br.com.barbeariadopra.service.ProfissionalService;
import lombok.RequiredArgsConstructor;

// Define a classe como um controller REST e injeta as dependências necessárias
@RestController
@RequiredArgsConstructor // Com isso, não precisa de @Autowired nos atributos finais
@RequestMapping(value = "/profissional")
public class ProfissionalController {
    // Injeta o serviço responsável pela lógica de profissionais
    private final ProfissionalService profissionalService;

    // Lista profissionais com filtros opcionais de nome, email ou telefone
    @GetMapping
    public ResponseEntity<List<ProfissionalEntity>> listarComFiltros(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String telefone
    ) {
        List<ProfissionalEntity> lista = profissionalService.listarComFiltros(nome, email, telefone);
        return ResponseEntity.ok(lista);
    }

    // Busca um profissional pelo ID
    @GetMapping("/{idProfissional}")
    public ResponseEntity<ProfissionalEntity> buscarPorId(@PathVariable int idProfissional) {
        ProfissionalEntity profissional = profissionalService.buscarPorId(idProfissional);
        if (profissional != null) {
            return ResponseEntity.ok(profissional);
        }
        return ResponseEntity.notFound().build();
    }

    // Cria um novo profissional
    @PostMapping
    public ResponseEntity<ProfissionalEntity> incluir(@RequestBody ProfissionalEntity profissional) {
        ProfissionalEntity novo = profissionalService.incluir(profissional);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED); // Retorna 201 se criado
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna 400 se erro
        }
    }

    // Edita os dados de um profissional já existente pelo ID
    @PutMapping("/{idProfissional}")
    public ResponseEntity<ProfissionalEntity> editar(@PathVariable int idProfissional, @RequestBody ProfissionalEntity profissional) {
        ProfissionalEntity atualizado = profissionalService.editar(idProfissional, profissional);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK); // Retorna 200 se atualizado
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 se não encontrou
        }
    }

    // Exclui um profissional pelo ID
    @DeleteMapping("/{idProfissional}")
    public ResponseEntity<Void> excluir(@PathVariable int idProfissional) {
        profissionalService.excluir(idProfissional);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 sem conteúdo
    }
}
