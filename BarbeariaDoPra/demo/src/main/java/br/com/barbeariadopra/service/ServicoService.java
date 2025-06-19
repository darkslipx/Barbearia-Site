package br.com.barbeariadopra.service;

import br.com.barbeariadopra.entity.ServicoEntity;
import br.com.barbeariadopra.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public List<ServicoEntity> listar(String nome) {
        if (nome != null && !nome.trim().isEmpty()) {
            return servicoRepository.findByNomeContainingIgnoreCase(nome.trim());
        }
        return servicoRepository.findAll();
    }

    public ServicoEntity buscarPorId(Integer id) {
        return servicoRepository.findById(id).orElse(null);
    }

    public ServicoEntity salvar(ServicoEntity servico) {
        return servicoRepository.save(servico);
    }

    public ServicoEntity editar(Integer id, ServicoEntity novosDados) {
        ServicoEntity existente = buscarPorId(id);
        if (existente == null) return null;
        existente.setNome(novosDados.getNome());
        existente.setValor(novosDados.getValor());
        return servicoRepository.save(existente);
    }

    public void excluir(Integer id) {
        servicoRepository.deleteById(id);
    }
}
