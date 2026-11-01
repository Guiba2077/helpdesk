package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories.AbstractRepository;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository("businessUsuarioRepository")
public class UsuarioRepositoryImpl extends AbstractRepository<UsuarioModel> implements IUsuarioRepository {

    public UsuarioRepositoryImpl(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected String getTableName() {
        return "app.usuario";
    }

    @Override
    protected RowMapper<UsuarioModel> getRowMapper() {
        return (rs, rowNum) -> {
            UsuarioModel model = new UsuarioModel();
            model.setId(rs.getLong("id"));
            model.setNome(rs.getString("nome"));
            model.setEmail(rs.getString("email"));
            model.setSenha(rs.getString("senha_hash"));
            model.setTelefone(rs.getString("telefone"));
            model.setSetor(rs.getString("setor"));
            model.setPerfil(rs.getString("perfil"));
            model.setAtivo(rs.getBoolean("ativo"));
            Timestamp criadoEm = rs.getTimestamp("criado_em");
            if (criadoEm != null) {
                model.setDataCadastro(criadoEm.toLocalDateTime());
            }
            return model;
        };
    }

    @Override
    public Optional<UsuarioModel> buscarPorEmail(String email) {
        String sql = getSelectSql() + " WHERE LOWER(email) = LOWER(?) AND ativo = true";
        List<UsuarioModel> lista = jdbc.query(sql, getRowMapper(), email);
        return lista.stream().findFirst();
    }

    @Override
    public Optional<UsuarioModel> autenticarComPgcrypto(String email, String senhaPura) {
        // Utiliza a extensão pgcrypto do PostgreSQL: crypt(senha, senha_hash) = senha_hash
        String sql = getSelectSql()
                + " WHERE LOWER(email) = LOWER(?) AND ativo = true "
                + " AND senha_hash = crypt(?, senha_hash)";
        List<UsuarioModel> lista = jdbc.query(sql, getRowMapper(), email, senhaPura);
        return lista.stream().findFirst();
    }

    @Override
    public List<UsuarioModel> buscarPorNome(String nome) {
        String sql = getSelectSql() + " WHERE nome ILIKE ? ORDER BY nome";
        return jdbc.query(sql, getRowMapper(), "%" + nome + "%");
    }

    @Override
    public UsuarioModel salvar(UsuarioModel entity) {
        String sql = """
                INSERT INTO app.usuario (nome, email, senha_hash, telefone, setor, perfil, ativo)
                VALUES (?, ?, crypt(?, gen_salt('bf')), ?, ?, ?, true)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getSenha() != null ? entity.getSenha() : "123456");
            ps.setString(4, entity.getTelefone());
            ps.setString(5, entity.getSetor());
            ps.setString(6, entity.getPerfil() != null ? entity.getPerfil().toUpperCase() : "OPERADOR");
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            entity.setId(generatedId.longValue());
        }
        return buscarPorId(entity.getId()).orElse(entity);
    }

    @Override
    public void atualizar(UsuarioModel entity) {
        String sql = """
                UPDATE app.usuario
                   SET nome = ?,
                       email = ?,
                       telefone = ?,
                       setor = ?,
                       perfil = ?,
                       atualizado_em = now()
                 WHERE id = ?
                """;
        jdbc.update(sql,
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getSetor(),
                entity.getPerfil() != null ? entity.getPerfil().toUpperCase() : "OPERADOR",
                entity.getId());
    }
}

