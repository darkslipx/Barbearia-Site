package br.com.barbeariadopra.controller;

import br.com.barbeariadopra.dto.LoginRequest;
import br.com.barbeariadopra.entity.PessoaEntity;
import br.com.barbeariadopra.repository.PessoaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final PessoaRepository pessoaRepository;

    public LoginController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        PessoaEntity usuario = pessoaRepository.findByEmail(loginRequest.getEmail());
        if (usuario != null && usuario.getSenha().equals(loginRequest.getSenha())) {
            // Retorne só os dados necessários, não exponha senha!
            return ResponseEntity.ok(new UsuarioResposta(
                usuario.getIdPessoa(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getNivel()
            ));
        }
        return ResponseEntity.status(401).body("E-mail ou senha incorretos.");
    }

    // Classe interna para resposta (evita expor a senha)
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
