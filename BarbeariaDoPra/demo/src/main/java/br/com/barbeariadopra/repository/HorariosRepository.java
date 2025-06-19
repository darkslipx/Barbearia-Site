package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.HorariosEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface HorariosRepository extends JpaRepository<HorariosEntity, Integer> {
    List<HorariosEntity> findByBloqueado(Boolean bloqueado);
    List<HorariosEntity> findByProfissional_IdProfissionalAndBloqueado(Integer idProfissional, Boolean bloqueado);

    // Horários realmente disponíveis para o profissional (não bloqueados e não agendados)
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
