package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.ServicoEntity;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<ServicoEntity, Integer> {
    // Para filtros simples:
    List<ServicoEntity> findByNomeContainingIgnoreCase(String nome);
}
