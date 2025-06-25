package br.com.barbeariadopra.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import br.com.barbeariadopra.entity.ClienteEntity;
import br.com.barbeariadopra.entity.PessoaEntity;
import br.com.barbeariadopra.repository.ClienteRepository;
import br.com.barbeariadopra.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;

// Serviço responsável pela lógica de negócios relacionada a clientes
@Service
@RequiredArgsConstructor // injeta as dependências nos atributos finais sem precisar de @Autowired
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PessoaRepository pessoaRepository;

    // Lista clientes aplicando filtros opcionais de nome, email e telefone (filtrando em memória)
    public List<ClienteEntity> listarComFiltros(String nome, String email, String telefone) {
        List<ClienteEntity> lista = clienteRepository.findAll();
        // Filtra por nome se informado
        if (nome != null && !nome.isEmpty()) {
            lista = lista.stream()
                .filter(c -> c.getPessoa() != null && c.getPessoa().getNome() != null &&
                    c.getPessoa().getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        // Filtra por email se informado
        if (email != null && !email.isEmpty()) {
            lista = lista.stream()
                .filter(c -> c.getPessoa() != null && c.getPessoa().getEmail() != null &&
                    c.getPessoa().getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
        }
        // Filtra por telefone se informado
        if (telefone != null && !telefone.isEmpty()) {
            lista = lista.stream()
                .filter(c -> c.getTelefone() != null &&
                    c.getTelefone().contains(telefone))
                .collect(Collectors.toList());
        }
        return lista;
    }

    // Cadastra um novo cliente comum (define o nível como "CLIENTE")
    public ClienteEntity incluirComoCliente(ClienteEntity cliente) {
        if (cliente.getPessoa() != null) {
            cliente.getPessoa().setNivel("CLIENTE");
        }
        return clienteRepository.save(cliente);
    }

    // Cadastra um novo cliente aceitando o nível informado (admin)
    public ClienteEntity incluirComoAdmin(ClienteEntity cliente) {
        return clienteRepository.save(cliente);
    }

    // Edita os dados do cliente e também atualiza dados da pessoa associada
    public ClienteEntity editar(Integer id, ClienteEntity novosDados) {
        ClienteEntity existente = clienteRepository.findById(id).orElse(null);
        if (existente != null && novosDados != null) {
            // Atualiza dados da pessoa vinculada (nome, email)
            PessoaEntity pessoaExistente = existente.getPessoa();
            PessoaEntity novaPessoa = novosDados.getPessoa();

            if (pessoaExistente != null && novaPessoa != null) {
                if (novaPessoa.getNome() != null) pessoaExistente.setNome(novaPessoa.getNome());
                if (novaPessoa.getEmail() != null) pessoaExistente.setEmail(novaPessoa.getEmail());
                // Pode atualizar outros campos se necessário...

                // Garante que o nível esteja definido
                if (pessoaExistente.getNivel() == null) {
                    pessoaExistente.setNivel("CLIENTE");
                }

                pessoaRepository.save(pessoaExistente);
            }

            // Atualiza telefone e data de nascimento do cliente
            if (novosDados.getTelefone() != null)
                existente.setTelefone(novosDados.getTelefone());
            if (novosDados.getDataNascimento() != null)
                existente.setDataNascimento(novosDados.getDataNascimento());

            return clienteRepository.save(existente);
        }
        return null;
    }

    // Busca cliente pelo ID da pessoa associada
    public ClienteEntity buscarPorPessoa(int idPessoa) {
        return clienteRepository.findByPessoa_IdPessoa(idPessoa);
    }

    // Busca cliente pelo ID do cliente
    public ClienteEntity buscarPorId(int id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // Lista todos os clientes cadastrados
    public List<ClienteEntity> listarTodos() {
        return clienteRepository.findAll();
    }

    // Exclui um cliente pelo ID
    public void excluir(Integer idCliente) {
        clienteRepository.deleteById(idCliente);
    }
}
