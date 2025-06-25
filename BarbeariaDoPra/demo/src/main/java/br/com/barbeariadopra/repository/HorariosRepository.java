package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.HorariosEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Repositório para acessar e manipular horários no banco de dados
@Repository
public interface HorariosRepository extends JpaRepository<HorariosEntity, Integer> {

    // Busca todos os horários pelo status de bloqueio (true ou false)
    List<HorariosEntity> findByBloqueado(Boolean bloqueado);

    // Busca todos os horários de um profissional específico pelo status de bloqueio
    List<HorariosEntity> findByProfissional_IdProfissionalAndBloqueado(Integer idProfissional, Boolean bloqueado);

    // Busca horários realmente disponíveis para agendamento:
    // - Do profissional informado
    // - Que não estão bloqueados
    // - Que não estão agendados (status diferente de CANCELADO ou RECUSADO) na data informada
    @Query("""
        SELECT h FROM HorariosEntity h
        WHERE h.profissional.idProfissional = :idProfissional
          AND h.bloqueado = false
          AND h.idHorario NOT IN (
            SELECT a.horario.idHorario
            FROM AgendamentoEntity a
            WHERE a.status NOT IN ('CANCELADO', 'RECUSADO')
              AND a.data = :data
          )
    """)
    List<HorariosEntity> buscarHorariosDisponiveisParaAgendamento(
        @Param("idProfissional") Integer idProfissional,
        @Param("data") java.time.LocalDate data
    );
}
