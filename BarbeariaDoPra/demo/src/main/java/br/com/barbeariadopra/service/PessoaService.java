package br.com.barbeariadopra.service;

import java.util.List;
import org.springframework.stereotype.Service;
import br.com.barbeariadopra.entity.PessoaEntity;
import br.com.barbeariadopra.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;

// Serviço responsável pela lógica de negócios relacionada à entidade Pessoa
@Service
@RequiredArgsConstructor
public class PessoaService {

    // Repositório para acesso ao banco de dados de pessoas
    private final PessoaRepository pessoaRepository;

    // Salva uma nova pessoa no banco de dados
    public PessoaEntity incluir(PessoaEntity pessoa) {
        return pessoaRepository.save(pessoa);
    }

    // Busca uma pessoa pelo ID
    public PessoaEntity buscarPorId(int id) {
        return pessoaRepository.findById(id).orElse(null);
    }

    // Edita os dados (nome, email, nível) de uma pessoa existente
    public PessoaEntity editar(int idPessoa, PessoaEntity pessoa) {
        PessoaEntity existente = pessoaRepository.findById(idPessoa).orElse(null);
        if (existente != null) {
            if (pessoa.getNome() != null) {
                existente.setNome(pessoa.getNome());
            }
            if (pessoa.getEmail() != null) {
                existente.setEmail(pessoa.getEmail());
            }
            if (pessoa.getNivel() != null) {
                existente.setNivel(pessoa.getNivel());
            }
            // A senha NÃO é alterada aqui!
            return pessoaRepository.save(existente);
        }
        return null;
    }

    // Altera a senha de uma pessoa, conferindo a senha atual antes
    public boolean alterarSenha(int idPessoa, String senhaAtual, String novaSenha) {
        PessoaEntity pessoa = pessoaRepository.findById(idPessoa).orElse(null);
        if (pessoa == null) return false;
        if (!pessoa.getSenha().equals(senhaAtual)) return false;
        pessoa.setSenha(novaSenha);
        pessoaRepository.save(pessoa);
        return true;
    }

    // Lista todas as pessoas cadastradas
    public List<PessoaEntity> listarTodos() {
        return pessoaRepository.findAll();
    }

    // Exclui uma pessoa pelo ID
    public void excluir(Integer idPessoa) {
        pessoaRepository.deleteById(idPessoa);
    }
}
