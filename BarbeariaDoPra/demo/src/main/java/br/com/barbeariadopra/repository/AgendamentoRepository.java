package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.AgendamentoEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<AgendamentoEntity, Integer> {
    List<AgendamentoEntity> findByCliente_IdCliente(Integer idCliente);
    List<AgendamentoEntity> findByProfissional_IdProfissionalAndData(Integer profissionalId, LocalDate data);
}
