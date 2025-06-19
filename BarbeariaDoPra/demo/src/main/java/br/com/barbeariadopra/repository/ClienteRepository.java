package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.barbeariadopra.entity.ClienteEntity;

@Repository
public interface ClienteRepository
 extends JpaRepository<ClienteEntity, Integer>{
    ClienteEntity findByPessoa_IdPessoa(Integer idPessoa);

}