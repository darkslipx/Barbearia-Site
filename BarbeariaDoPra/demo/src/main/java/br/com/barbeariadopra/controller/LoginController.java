package br.com.barbeariadopra.controller;

import br.com.barbeariadopra.dto.LoginRequest;
import br.com.barbeariadopra.entity.PessoaEntity;
import br.com.barbeariadopra.repository.PessoaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Marca esta classe como um controller REST (responde a requisições HTTP)
@RequestMapping("/login") // Define o prefixo de rota para todos os endpoints deste controller
public class LoginController {
    private final PessoaRepository pessoaRepository;

    // Injeta o repositório de pessoas pelo construtor
    public LoginController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    // Endpoint para autenticação/login do usuário
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Busca usuário pelo e-mail no banco
        PessoaEntity usuario = pessoaRepository.findByEmail(loginRequest.getEmail());
        // Se encontrou o usuário e a senha está correta
        if (usuario != null && usuario.getSenha().equals(loginRequest.getSenha())) {
            // Retorna os dados do usuário autenticado (NÃO retorna a senha!)
            return ResponseEntity.ok(new UsuarioResposta(
                usuario.getIdPessoa(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getNivel()
            ));
        }
        // Se não encontrou ou senha está errada, retorna 401 (não autorizado)
        return ResponseEntity.status(401).body("E-mail ou senha incorretos.");
    }

    // Classe interna para resposta: evita expor a senha para o front-end
    public static class UsuarioResposta {
        public Integer id;
        public String nome;
        public String email;
        public String nivel;

        public UsuarioResposta(Integer id, String nome, String email, String nivel) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.nivel = nivel;
        }
    }
}
