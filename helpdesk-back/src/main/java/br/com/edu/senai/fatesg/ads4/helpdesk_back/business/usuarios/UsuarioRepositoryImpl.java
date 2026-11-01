package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository("businessUsuarioRepository")
public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private final UsuarioJpaRepository jpa;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<UsuarioModel> buscarPorId(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<UsuarioModel> listarTodos() {
        return jpa.findByAtivoTrueOrderByIdDesc();
    }

    @Override
    public List<UsuarioModel> listarTodosIncluindoInativos() {
        return jpa.findAllByOrderByIdDesc();
    }

    @Override
    public Optional<UsuarioModel> buscarPorEmail(String email) {
        return jpa.findByEmailIgnoreCaseAndAtivoTrue(email);
    }

    @Override
    public Optional<UsuarioModel> autenticarComPgcrypto(String email, String senhaPura) {
        return jpa.autenticarComPgcrypto(email, senhaPura);
    }

    @Override
    public List<UsuarioModel> buscarPorNome(String nome) {
        return jpa.buscarPorNome(nome);
    }

    @Override
    @Transactional
    public UsuarioModel salvar(UsuarioModel entity) {
        String senha = entity.getSenha() != null ? entity.getSenha() : "123456";
        String perfil = entity.getPerfil() != null ? entity.getPerfil().toUpperCase() : "OPERADOR";
        Long novoId = jpa.inserirComPgcrypto(
                entity.getNome(),
                entity.getEmail(),
                senha,
                entity.getTelefone(),
                entity.getSetor(),
                perfil);
        return jpa.findById(novoId).orElse(entity);
    }

    @Override
    @Transactional
    public void atualizar(UsuarioModel entity) {
        UsuarioModel existente = jpa.findById(entity.getId()).orElse(entity);
        existente.setNome(entity.getNome());
        existente.setEmail(entity.getEmail());
        existente.setTelefone(entity.getTelefone());
        existente.setSetor(entity.getSetor());
        if (entity.getPerfil() != null) {
            existente.setPerfil(entity.getPerfil().toUpperCase());
        }
        jpa.save(existente);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        jpa.inativar(id);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        jpa.alternarStatus(id);
    }
}
