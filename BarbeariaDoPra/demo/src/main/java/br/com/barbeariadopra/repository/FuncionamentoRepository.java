package br.com.barbeariadopra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.barbeariadopra.entity.FuncionamentoEntity;
import br.com.barbeariadopra.enums.DiaSemana;

@Repository
public interface FuncionamentoRepository extends JpaRepository<FuncionamentoEntity, Integer> {
    List<FuncionamentoEntity> findByProfissional_IdProfissional(Integer idProfissional);
    List<FuncionamentoEntity> findByProfissional_IdProfissionalAndDiaSemana(Integer idProfissional, DiaSemana diaSemana);
}
