package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.BusinessException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.EntidadeNaoEncontradaException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.GenericService;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("businessUsuarioService")
public class UsuarioService extends GenericService<UsuarioModel, UsuarioDTO, IUsuarioRepository>
        implements IUsuarioService {

    private final UsuarioMapper mapper;
    private final IUsuarioValidation validation;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            IUsuarioRepository repository,
            UsuarioMapper mapper,
            IUsuarioValidation validation,
            PasswordEncoder passwordEncoder) {
        super(repository);
        this.mapper = mapper;
        this.validation = validation;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected UsuarioDTO toDTO(UsuarioModel model) {
        return mapper.toDto(model);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new BusinessException("E-mail e senha são obrigatórios para o login.");
        }
        // 1. Tenta autenticar direto no PostgreSQL usando pgcrypto (crypt)
        Optional<UsuarioModel> autenticadoPg = repository.autenticarComPgcrypto(email.trim(), senha);
        if (autenticadoPg.isPresent()) {
            Usuario usuarioDominio = UsuarioFactory.fromModel(autenticadoPg.get());
            UsuarioDTO dto = mapper.toDto(autenticadoPg.get());
            dto.setPerfil(usuarioDominio.getPerfil().name());
            return dto;
        }

        // 2. Fallback caso o hash tenha sido gerado via BCrypt do Spring Security
        UsuarioModel usuario = repository.buscarPorEmail(email.trim())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas ou usuário inativo."));

        if (usuario.getSenha() != null && passwordEncoder.matches(senha, usuario.getSenha())) {
            return mapper.toDto(usuario);
        }

        throw new BusinessException("Credenciais inválidas ou usuário inativo.");
    }

    @Override
    @Transactional
    public UsuarioDTO salvar(UsuarioModel model) {
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioModel model) {
        UsuarioModel existente = repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com ID: " + id));
        model.setId(existente.getId());
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> {
            repository.atualizar(model);
            return buscarPorId(id);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome == null ? "" : nome.trim())
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}

