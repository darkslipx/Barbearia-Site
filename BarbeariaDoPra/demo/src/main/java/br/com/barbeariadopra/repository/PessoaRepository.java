package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.barbeariadopra.entity.PessoaEntity;

// Repositório para acessar e manipular pessoas no banco de dados
public interface PessoaRepository extends JpaRepository<PessoaEntity, Integer> {

    // Busca uma pessoa pelo e-mail (usado normalmente em autenticação/login)
    PessoaEntity findByEmail(String email);

}
