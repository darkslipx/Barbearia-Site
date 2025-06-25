package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.ProfissionalEntity;

// Repositório para acessar e manipular profissionais no banco de dados
@Repository
public interface ProfissionalRepository extends JpaRepository<ProfissionalEntity, Integer> {
    // Métodos CRUD (create, read, update, delete) já disponíveis por padrão pelo JpaRepository

    // Pode-se adicionar métodos personalizados aqui, se necessário
}
