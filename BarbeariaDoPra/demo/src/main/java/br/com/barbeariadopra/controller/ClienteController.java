package br.com.barbeariadopra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.barbeariadopra.entity.ClienteEntity;
import br.com.barbeariadopra.service.ClienteService;
import lombok.RequiredArgsConstructor;

// Marca esta classe como um Controller REST do Spring
@RestController
@RequiredArgsConstructor // Gera construtor com os campos finais (injeção)
@RequestMapping(value = "/clientes") // Todos os endpoints começam com /clientes
public class ClienteController {
    private final ClienteService clienteService; // Injeta o service de Cliente

    // ================== LISTA CLIENTES COM FILTROS ==================
    @GetMapping
    public ResponseEntity<List<ClienteEntity>> listarComFiltros(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone
    ) {
        // Chama o service para buscar clientes filtrando por nome, email e telefone
        List<ClienteEntity> lista = clienteService.listarComFiltros(nome, email, telefone);
        return ResponseEntity.ok(lista); // Retorna 200 OK + lista encontrada (pode ser vazia)
    }

    // ================== BUSCAR CLIENTE POR ID ==================
    @GetMapping("/{idCliente}")
    public ResponseEntity<ClienteEntity> buscarPorId(@PathVariable int idCliente) {
        ClienteEntity cliente = clienteService.buscarPorId(idCliente);
        if (cliente != null) {
            return ResponseEntity.ok(cliente); // Retorna 200 + cliente se encontrado
        }
        return ResponseEntity.notFound().build(); // Retorna 404 se não encontrar
    }

    // ================== BUSCAR CLIENTE PELO ID DA PESSOA ==================
    @GetMapping("/pessoa/{idPessoa}")
    public ResponseEntity<ClienteEntity> buscarPorPessoa(@PathVariable int idPessoa) {
        ClienteEntity cliente = clienteService.buscarPorPessoa(idPessoa);
        if (cliente != null) return ResponseEntity.ok(cliente);
        return ResponseEntity.notFound().build(); // Retorna 404 se não encontrar
    }

    // ================== CADASTRAR UM NOVO CLIENTE ==================
    @PostMapping
    public ResponseEntity<ClienteEntity> incluir(@RequestBody ClienteEntity cliente) {
        // Garante que o novo cadastro terá o nível CLIENTE
        if (cliente.getPessoa() != null) {
            cliente.getPessoa().setNivel("CLIENTE"); // Sempre CLIENTE
        }
        ClienteEntity novo = clienteService.incluirComoCliente(cliente);
        if (novo != null) {
            // Sucesso: retorna 201 CREATED e o cliente cadastrado
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            // Falha: retorna 400 BAD REQUEST
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // ================== CADASTRAR CLIENTE COMO ADMIN ==================
    @PostMapping("/admin")
    public ResponseEntity<ClienteEntity> incluirAdmin(@RequestBody ClienteEntity cliente) {
        // Cadastra um novo cliente com nível ADMIN (caso necessário)
        ClienteEntity novo = clienteService.incluirComoAdmin(cliente);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // ================== EDITAR UM CLIENTE EXISTENTE ==================
    @PutMapping("/{idCliente}")
    public ResponseEntity<ClienteEntity> editar(@PathVariable int idCliente, @RequestBody ClienteEntity cliente) {
        ClienteEntity atualizado = clienteService.editar(idCliente, cliente);
        if (atualizado != null) {
            // Sucesso: retorna 200 OK e cliente atualizado
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            // Falha: retorna 404 NOT FOUND
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ================== EXCLUIR UM CLIENTE ==================
    @DeleteMapping("/{idCliente}")
    public ResponseEntity<Void> excluir(@PathVariable int idCliente) {
        clienteService.excluir(idCliente);
        // Sucesso: retorna 204 NO CONTENT (padrão para exclusão)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
