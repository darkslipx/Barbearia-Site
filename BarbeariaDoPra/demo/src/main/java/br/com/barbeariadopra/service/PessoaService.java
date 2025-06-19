package br.com.barbeariadopra.service;

import java.util.List;
import org.springframework.stereotype.Service;
import br.com.barbeariadopra.entity.PessoaEntity;
import br.com.barbeariadopra.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaEntity incluir(PessoaEntity pessoa) {
        return pessoaRepository.save(pessoa);
    }

    public PessoaEntity buscarPorId(int id) {
        return pessoaRepository.findById(id).orElse(null);
    }

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

    public boolean alterarSenha(int idPessoa, String senhaAtual, String novaSenha) {
        PessoaEntity pessoa = pessoaRepository.findById(idPessoa).orElse(null);
        if (pessoa == null) return false;
        if (!pessoa.getSenha().equals(senhaAtual)) return false;
        pessoa.setSenha(novaSenha);
        pessoaRepository.save(pessoa);
        return true;
    }

    public List<PessoaEntity> listarTodos() {
        return pessoaRepository.findAll();
    }

    public void excluir(Integer idPessoa) {
        pessoaRepository.deleteById(idPessoa);
    }
}
