package br.com.barbeariadopra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.barbeariadopra.entity.ClienteEntity;
import br.com.barbeariadopra.service.ClienteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteEntity>> listarComFiltros(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone
    ) {
        List<ClienteEntity> lista = clienteService.listarComFiltros(nome, email, telefone);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<ClienteEntity> buscarPorId(@PathVariable int idCliente) {
        ClienteEntity cliente = clienteService.buscarPorId(idCliente);
        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        }
        return ResponseEntity.notFound().build();
    }

        // No ClienteController
    @GetMapping("/pessoa/{idPessoa}")
    public ResponseEntity<ClienteEntity> buscarPorPessoa(@PathVariable int idPessoa) {
        ClienteEntity cliente = clienteService.buscarPorPessoa(idPessoa);
        if (cliente != null) return ResponseEntity.ok(cliente);
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<ClienteEntity> incluir(@RequestBody ClienteEntity cliente) {
        if (cliente.getPessoa() != null) {
            cliente.getPessoa().setNivel("CLIENTE"); // Sempre CLIENTE
        }
        ClienteEntity novo = clienteService.incluirComoCliente(cliente);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<ClienteEntity> incluirAdmin(@RequestBody ClienteEntity cliente) {
        ClienteEntity novo = clienteService.incluirComoAdmin(cliente);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idCliente}")
    public ResponseEntity<ClienteEntity> editar(@PathVariable int idCliente, @RequestBody ClienteEntity cliente) {
        ClienteEntity atualizado = clienteService.editar(idCliente, cliente);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{idCliente}")
    public ResponseEntity<Void> excluir(@PathVariable int idCliente) {
        clienteService.excluir(idCliente);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
