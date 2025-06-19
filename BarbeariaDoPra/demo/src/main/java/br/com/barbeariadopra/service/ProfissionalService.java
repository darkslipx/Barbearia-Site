package br.com.barbeariadopra.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import br.com.barbeariadopra.entity.ProfissionalEntity;
import br.com.barbeariadopra.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor //ao usar isso não precisa do @Autowired no objeto
public class ProfissionalService {

    @SuppressWarnings("unused")
    private final ProfissionalRepository profissionalRepository;

   public ProfissionalEntity incluir(ProfissionalEntity profissional) {
    profissional.getPessoa().setNivel("PROFISSIONAL"); // Garante nível
    return profissionalRepository.save(profissional);
}



    public ProfissionalEntity buscarPorId(int id) {
        return profissionalRepository.findById(id).orElse(null);
    }
    
    public List<ProfissionalEntity> listarComFiltros(String nome, String email, String telefone) {
    List<ProfissionalEntity> lista = listarTodos(); // ou repository.findAll()
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

  public ProfissionalEntity editar(Integer id, ProfissionalEntity novosDados) {
    ProfissionalEntity existente = profissionalRepository.findById(id).orElse(null);
    if (existente != null) {
        // Atualiza telefone e data de nascimento normalmente
        existente.setTelefone(novosDados.getTelefone());
        existente.setDataNascimento(novosDados.getDataNascimento());

        // Atualiza somente os campos da pessoa, sem trocar a referência
        if (existente.getPessoa() != null && novosDados.getPessoa() != null) {
            existente.getPessoa().setNome(novosDados.getPessoa().getNome());
            existente.getPessoa().setEmail(novosDados.getPessoa().getEmail());
            // Para alterar senha, descomente abaixo:
            // existente.getPessoa().setSenha(novosDados.getPessoa().getSenha());
        }

        return profissionalRepository.save(existente);
    }
    return null;
}


    public List<ProfissionalEntity> listarTodos() {
        return profissionalRepository.findAll();
    }
    public void excluir(Integer idProfissional) {
        profissionalRepository.deleteById(idProfissional);
    }
}