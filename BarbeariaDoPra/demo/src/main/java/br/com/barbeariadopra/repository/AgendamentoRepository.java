package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.AgendamentoEntity;

import java.time.LocalDate;
import java.util.List;

// Repositório para acessar e manipular agendamentos no banco de dados
@Repository
public interface AgendamentoRepository extends JpaRepository<AgendamentoEntity, Integer> {

    // Busca todos os agendamentos de um cliente específico pelo ID do cliente
    List<AgendamentoEntity> findByCliente_IdCliente(Integer idCliente);

    // Busca todos os agendamentos de um profissional em uma data específica
    List<AgendamentoEntity> findByProfissional_IdProfissionalAndData(Integer profissionalId, LocalDate data);
}
