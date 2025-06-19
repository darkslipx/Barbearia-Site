package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.barbeariadopra.entity.ProfissionalEntity;

@Repository
public interface ProfissionalRepository
extends JpaRepository<ProfissionalEntity, Integer>{

}