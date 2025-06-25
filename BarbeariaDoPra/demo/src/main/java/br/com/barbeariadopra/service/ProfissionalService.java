package br.com.barbeariadopra.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import br.com.barbeariadopra.entity.ProfissionalEntity;
import br.com.barbeariadopra.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;

// Serviço responsável pela lógica de negócios relacionada a profissionais
@Service
@RequiredArgsConstructor // injeta as dependências no construtor automaticamente (sem precisar de @Autowired)
public class ProfissionalService {

    @SuppressWarnings("unused")
    private final ProfissionalRepository profissionalRepository;

    // Cadastra um novo profissional e garante que o nível está correto
    public ProfissionalEntity incluir(ProfissionalEntity profissional) {
        profissional.getPessoa().setNivel("PROFISSIONAL"); // Garante que o nível está definido
        return profissionalRepository.save(profissional);
    }

    // Busca um profissional pelo ID
    public ProfissionalEntity buscarPorId(int id) {
        return profissionalRepository.findById(id).orElse(null);
    }

    // Lista profissionais com filtros opcionais (nome, email, telefone), filtrando em memória
    public List<ProfissionalEntity> listarComFiltros(String nome, String email, String telefone) {
        List<ProfissionalEntity> lista = listarTodos();
        if (nome != null && !nome.isEmpty()) {
            lista = lista.stream()
                .filter(p -> p.getPessoa() != null && p.getPessoa().getNome() != null &&
                    p.getPessoa().getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        if (email != null && !email.isEmpty()) {
            lista = lista.stream()
                .filter(p -> p.getPessoa() != null && p.getPessoa().getEmail() != null &&
                    p.getPessoa().getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
        }
        if (telefone != null && !telefone.isEmpty()) {
            lista = lista.stream()
                .filter(p -> p.getTelefone() != null && p.getTelefone().contains(telefone))
                .collect(Collectors.toList());
        }
        return lista;
    }

    // Edita os dados do profissional e os dados da pessoa associada (nome/email)
    public ProfissionalEntity editar(Integer id, ProfissionalEntity novosDados) {
        ProfissionalEntity existente = profissionalRepository.findById(id).orElse(null);
        if (existente != null) {
            // Atualiza telefone e data de nascimento
            existente.setTelefone(novosDados.getTelefone());
            existente.setDataNascimento(novosDados.getDataNascimento());

            // Atualiza apenas os campos da pessoa já associada
            if (existente.getPessoa() != null && novosDados.getPessoa() != null) {
                existente.getPessoa().setNome(novosDados.getPessoa().getNome());
                existente.getPessoa().setEmail(novosDados.getPessoa().getEmail());
                // Para alterar senha, descomente a linha abaixo:
                // existente.getPessoa().setSenha(novosDados.getPessoa().getSenha());
            }

            return profissionalRepository.save(existente);
        }
        return null;
    }

    // Lista todos os profissionais cadastrados
    public List<ProfissionalEntity> listarTodos() {
        return profissionalRepository.findAll();
    }

    // Exclui um profissional pelo ID
    public void excluir(Integer idProfissional) {
        profissionalRepository.deleteById(idProfissional);
    }
}
