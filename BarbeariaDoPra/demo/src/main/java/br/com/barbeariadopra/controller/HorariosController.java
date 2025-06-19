package br.com.barbeariadopra.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.barbeariadopra.dto.HorarioStatusDTO;
import br.com.barbeariadopra.entity.HorariosEntity;
import br.com.barbeariadopra.service.HorariosService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/horario")
public class HorariosController {
    private final HorariosService horariosService;

   
    @GetMapping
public ResponseEntity<List<HorariosEntity>> listarHorariosComFiltros(
    @RequestParam(required = false) String profissional,
    @RequestParam(required = false) String diaSemana,
    @RequestParam(required = false) Boolean bloqueado,
    @RequestParam(required = false) String horaInicio,
    @RequestParam(required = false) String horaFim
) {
    List<HorariosEntity> lista = horariosService.listarComFiltros(profissional, diaSemana, bloqueado, horaInicio, horaFim);
    return ResponseEntity.ok(lista);
}



@GetMapping("/com-status")
public ResponseEntity<List<HorarioStatusDTO>> listarHorariosComStatus() {
    return ResponseEntity.ok(horariosService.listarHorariosComStatus());
}


    @GetMapping("/bloqueados")
    public ResponseEntity<List<HorariosEntity>> listarBloqueados() {
        return ResponseEntity.ok(horariosService.listarBloqueados());
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<HorariosEntity>> listarDisponiveis() {
        return ResponseEntity.ok(horariosService.listarDisponiveis());
    }

    @GetMapping("/profissional/{idProfissional}/bloqueados")
    public ResponseEntity<List<HorariosEntity>> listarBloqueadosPorProfissional(@PathVariable Integer idProfissional) {
        return ResponseEntity.ok(horariosService.listarBloqueadosPorProfissional(idProfissional));
    }

        // Exemplo:
    @GetMapping("/profissional/{idProfissional}/disponiveis")
    public ResponseEntity<List<HorariosEntity>> listarDisponiveisPorProfissional(@PathVariable Integer idProfissional) {
        return ResponseEntity.ok(horariosService.listarDisponiveisPorProfissional(idProfissional));
    }
    @GetMapping("/profissional/{idProfissional}/livres")
    public ResponseEntity<List<HorariosEntity>> horariosLivresParaAgendamento(
        @PathVariable Integer idProfissional,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        return ResponseEntity.ok(horariosService.horariosDisponiveisParaAgendamento(idProfissional, data));
    }


    @GetMapping("/{idHorario}")
    public ResponseEntity<HorariosEntity> buscarPorId(@PathVariable int idHorario) {
        HorariosEntity horario = horariosService.buscarPorId(idHorario);
        if (horario != null) {
            return ResponseEntity.ok(horario);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<HorariosEntity> incluir(@RequestBody HorariosEntity horario) {
        HorariosEntity novo = horariosService.incluir(horario);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idHorario}")
    public ResponseEntity<HorariosEntity> editar(@PathVariable int idHorario, @RequestBody HorariosEntity horario) {
        HorariosEntity atualizado = horariosService.editar(idHorario, horario);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{idHorario}/bloquear")
    public ResponseEntity<HorariosEntity> bloquearHorario(@PathVariable Integer idHorario) {
        HorariosEntity bloqueado = horariosService.bloquearHorario(idHorario);
        if (bloqueado != null) {
            return new ResponseEntity<>(bloqueado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{idHorario}/desbloquear")
public ResponseEntity<HorariosEntity> desbloquearHorario(@PathVariable Integer idHorario) {
    HorariosEntity desbloqueado = horariosService.desbloquearHorario(idHorario);
    if (desbloqueado != null) {
        return new ResponseEntity<>(desbloqueado, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}


    @DeleteMapping("/{idHorario}")
    public ResponseEntity<Void> excluir(@PathVariable int idHorario) {
        horariosService.excluir(idHorario);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
