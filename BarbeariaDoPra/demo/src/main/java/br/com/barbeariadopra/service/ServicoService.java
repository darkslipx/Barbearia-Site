package br.com.barbeariadopra.service;

import br.com.barbeariadopra.entity.ServicoEntity;
import br.com.barbeariadopra.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Serviço responsável pela lógica de negócios dos serviços cadastrados no sistema
@Service
@RequiredArgsConstructor
public class ServicoService {

    // Repositório de acesso aos dados de serviço
    private final ServicoRepository servicoRepository;

    // Lista serviços cadastrados, com filtro opcional por nome (case-insensitive, contém)
    public List<ServicoEntity> listar(String nome) {
        if (nome != null && !nome.trim().isEmpty()) {
            return servicoRepository.findByNomeContainingIgnoreCase(nome.trim());
        }
        return servicoRepository.findAll();
    }

    // Busca um serviço pelo seu ID
    public ServicoEntity buscarPorId(Integer id) {
        return servicoRepository.findById(id).orElse(null);
    }

    // Salva um novo serviço no banco de dados
    public ServicoEntity salvar(ServicoEntity servico) {
        return servicoRepository.save(servico);
    }

    // Edita os dados (nome e valor) de um serviço já existente
    public ServicoEntity editar(Integer id, ServicoEntity novosDados) {
        ServicoEntity existente = buscarPorId(id);
        if (existente == null) return null;
        existente.setNome(novosDados.getNome());
        existente.setValor(novosDados.getValor());
        return servicoRepository.save(existente);
    }

    // Exclui um serviço pelo ID
    public void excluir(Integer id) {
        servicoRepository.deleteById(id);
    }
}
