package br.com.barbeariadopra.dto;

import lombok.Getter;
import lombok.Setter;

// DTO utilizado para receber os dados de login na autenticação do usuário
@Getter 
@Setter 
public class LoginRequest {
    private String email; // E-mail do usuário para login
    private String senha; // Senha do usuário para login
}
