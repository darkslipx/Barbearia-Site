package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.barbeariadopra.entity.PessoaEntity;

public interface PessoaRepository extends JpaRepository<PessoaEntity, Integer> {
    PessoaEntity findByEmail(String email);
}
