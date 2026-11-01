package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

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

@Repository("businessTecnicoRepository")
public class TecnicoRepositoryImpl extends AbstractRepository<TecnicoModel> implements ITecnicoRepository {

    public TecnicoRepositoryImpl(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected String getTableName() {
        return "app.tecnico";
    }

    @Override
    protected String getSelectSql() {
        return """
                SELECT t.id,
                       t.usuario_id,
                       u.nome,
                       u.email,
                       u.telefone,
                       u.setor,
                       t.matricula,
                       t.especialidade,
                       t.nivel_atendimento,
                       t.disponivel,
                       t.ativo,
                       t.criado_em
                  FROM app.tecnico t
                  JOIN app.usuario u ON u.id = t.usuario_id
                """;
    }

    @Override
    protected RowMapper<TecnicoModel> getRowMapper() {
        return (rs, rowNum) -> {
            TecnicoModel model = new TecnicoModel();
            model.setId(rs.getLong("id"));
            model.setUsuarioId(rs.getLong("usuario_id"));
            model.setNome(rs.getString("nome"));
            model.setEmail(rs.getString("email"));
            model.setTelefone(rs.getString("telefone"));
            model.setSetor(rs.getString("setor"));
            model.setMatricula(rs.getString("matricula"));
            model.setEspecialidade(rs.getString("especialidade"));
            model.setNivelAtendimento(rs.getString("nivel_atendimento"));
            model.setDisponivel(rs.getBoolean("disponivel"));
            model.setAtivo(rs.getBoolean("ativo"));
            Timestamp criadoEm = rs.getTimestamp("criado_em");
            if (criadoEm != null) {
                model.setDataCadastro(criadoEm.toLocalDateTime());
            }
            return model;
        };
    }

    @Override
    public Optional<TecnicoModel> buscarPorId(Long id) {
        List<TecnicoModel> result = jdbc.query(getSelectSql() + " WHERE t.id = ?", getRowMapper(), id);
        return result.stream().findFirst();
    }

    @Override
    public List<TecnicoModel> listarTodos() {
        return jdbc.query(getSelectSql() + " WHERE t.ativo = true ORDER BY u.nome", getRowMapper());
    }

    @Override
    public List<TecnicoModel> listarTodosIncluindoInativos() {
        return jdbc.query(getSelectSql() + " ORDER BY u.nome", getRowMapper());
    }

    @Override
    public List<TecnicoModel> listarDisponiveis() {
        return jdbc.query(
                getSelectSql() + " WHERE t.ativo = true AND t.disponivel = true ORDER BY u.nome",
                getRowMapper());
    }

    @Override
    public List<Especialidade> listarEspecialidades() {
        return List.of(
                new Especialidade("INFRA_REDE", "Infraestrutura e Redes", "N2"),
                new Especialidade("DBA_POSTGRES", "Administração de Banco de Dados PostgreSQL", "N3"),
                new Especialidade("SUPORTE_N1", "Service Desk e Suporte ao Usuário", "N1"),
                new Especialidade("SEGURANCA_TI", "Segurança da Informação e Acessos", "N3"),
                new Especialidade("HARDWARE", "Manutenção de Hardware e Estações", "N1"));
    }

    @Override
    public TecnicoModel salvar(TecnicoModel entity) {
        String sql = """
                INSERT INTO app.tecnico (usuario_id, matricula, especialidade, nivel_atendimento, disponivel, ativo)
                VALUES (?, ?, ?, ?, ?, true)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, entity.getUsuarioId());
            ps.setString(2, entity.getMatricula());
            ps.setString(3, entity.getEspecialidade());
            ps.setString(4, entity.getNivelAtendimento() != null ? entity.getNivelAtendimento() : "N1");
            ps.setBoolean(5, entity.isDisponivel());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            entity.setId(generatedId.longValue());
        }
        return buscarPorId(entity.getId()).orElse(entity);
    }

    @Override
    public void atualizar(TecnicoModel entity) {
        String sql = """
                UPDATE app.tecnico
                   SET matricula = ?,
                       especialidade = ?,
                       nivel_atendimento = ?,
                       disponivel = ?
                 WHERE id = ?
                """;
        jdbc.update(sql,
                entity.getMatricula(),
                entity.getEspecialidade(),
                entity.getNivelAtendimento(),
                entity.isDisponivel(),
                entity.getId());
    }
}

