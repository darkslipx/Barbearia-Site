package br.com.barbeariadopra.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import br.com.barbeariadopra.entity.ClienteEntity;
import br.com.barbeariadopra.entity.PessoaEntity;
import br.com.barbeariadopra.repository.ClienteRepository;
import br.com.barbeariadopra.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // já injeta os final sem precisar de @Autowired
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PessoaRepository pessoaRepository;

    public List<ClienteEntity> listarComFiltros(String nome, String email, String telefone) {
        List<ClienteEntity> lista = clienteRepository.findAll();
        if (nome != null && !nome.isEmpty()) {
            lista = lista.stream()
                .filter(c -> c.getPessoa() != null && c.getPessoa().getNome() != null &&
                    c.getPessoa().getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        if (email != null && !email.isEmpty()) {
            lista = lista.stream()
                .filter(c -> c.getPessoa() != null && c.getPessoa().getEmail() != null &&
                    c.getPessoa().getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
        }
        if (telefone != null && !telefone.isEmpty()) {
            lista = lista.stream()
                .filter(c -> c.getTelefone() != null &&
                    c.getTelefone().contains(telefone))
                .collect(Collectors.toList());
        }
        return lista;
    }

    // Cadastro normal (cliente comum)
    public ClienteEntity incluirComoCliente(ClienteEntity cliente) {
        if (cliente.getPessoa() != null) {
            cliente.getPessoa().setNivel("CLIENTE");
        }
        return clienteRepository.save(cliente);
    }

    // Cadastro admin (aceita o nível que vier)
    public ClienteEntity incluirComoAdmin(ClienteEntity cliente) {
        return clienteRepository.save(cliente);
    }

    // Edição do cliente e dos dados da pessoa associada
   public ClienteEntity editar(Integer id, ClienteEntity novosDados) {
    ClienteEntity existente = clienteRepository.findById(id).orElse(null);
    if (existente != null && novosDados != null) {
        // Atualiza dados da pessoa associada (nome/email)
        PessoaEntity pessoaExistente = existente.getPessoa();
        PessoaEntity novaPessoa = novosDados.getPessoa();

        if (pessoaExistente != null && novaPessoa != null) {
            if (novaPessoa.getNome() != null) pessoaExistente.setNome(novaPessoa.getNome());
            if (novaPessoa.getEmail() != null) pessoaExistente.setEmail(novaPessoa.getEmail());
            // Atualize outros campos se quiser...

            // GARANTA O NÍVEL
            if (pessoaExistente.getNivel() == null) {
                pessoaExistente.setNivel("CLIENTE");
            }

            pessoaRepository.save(pessoaExistente);
        }

        // Atualiza dados do cliente (telefone, data de nascimento)
        if (novosDados.getTelefone() != null)
            existente.setTelefone(novosDados.getTelefone());
        if (novosDados.getDataNascimento() != null)
            existente.setDataNascimento(novosDados.getDataNascimento());

        return clienteRepository.save(existente);
    }
    return null;
}

    public ClienteEntity buscarPorPessoa(int idPessoa) {
        return clienteRepository.findByPessoa_IdPessoa(idPessoa);
    }



    public ClienteEntity buscarPorId(int id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public List<ClienteEntity> listarTodos() {
        return clienteRepository.findAll();
    }

    public void excluir(Integer idCliente) {
        clienteRepository.deleteById(idCliente);
    }
}
