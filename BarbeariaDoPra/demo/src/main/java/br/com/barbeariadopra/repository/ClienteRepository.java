package br.com.barbeariadopra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.barbeariadopra.entity.ClienteEntity;

// Repositório para acessar e manipular clientes no banco de dados
@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Integer> {

    // Busca um cliente pelo ID da pessoa associada (chave estrangeira para PessoaEntity)
    ClienteEntity findByPessoa_IdPessoa(Integer idPessoa);

}
