package br.com.barbeariadopra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.barbeariadopra.entity.FuncionamentoEntity;
import br.com.barbeariadopra.enums.DiaSemana;

// Repositório para acessar e manipular registros de funcionamento no banco de dados
@Repository
public interface FuncionamentoRepository extends JpaRepository<FuncionamentoEntity, Integer> {

    // Busca todos os funcionamentos de um profissional específico pelo ID
    List<FuncionamentoEntity> findByProfissional_IdProfissional(Integer idProfissional);

    // Busca todos os funcionamentos de um profissional em um dia da semana específico
    List<FuncionamentoEntity> findByProfissional_IdProfissionalAndDiaSemana(Integer idProfissional, DiaSemana diaSemana);
}
