package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.ServicoEntity;

import java.util.List;

// Repositório para acessar e manipular serviços no banco de dados
@Repository
public interface ServicoRepository extends JpaRepository<ServicoEntity, Integer> {

    // Busca serviços cujo nome contenha o texto informado (ignorando letras maiúsculas/minúsculas)
    List<ServicoEntity> findByNomeContainingIgnoreCase(String nome);

    // Outros métodos CRUD já disponíveis por padrão pelo JpaRepository
}
