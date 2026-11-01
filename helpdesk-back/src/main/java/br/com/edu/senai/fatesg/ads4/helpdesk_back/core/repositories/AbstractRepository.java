package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Implementação base do padrão Repository usando Template Method e JdbcTemplate com Prepared Statements.
 * Suporta múltiplos schemas no PostgreSQL (ex: "app.usuario", "app.chamado", "audit.auditoria_prioridade").
 */
public abstract class AbstractRepository<T extends BaseModel> implements IGenericRepository<T> {

    protected final JdbcTemplate jdbc;

    protected AbstractRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    protected abstract String getTableName();

    protected abstract RowMapper<T> getRowMapper();

    protected String getSelectSql() {
        return "SELECT * FROM " + getTableName();
    }

    @Override
    public Optional<T> buscarPorId(Long id) {
        List<T> result = jdbc.query(getSelectSql() + " WHERE id = ?", getRowMapper(), id);
        return result.stream().findFirst();
    }

    @Override
    public List<T> listarTodos() {
        return jdbc.query(getSelectSql() + " WHERE ativo = true ORDER BY id DESC", getRowMapper());
    }

    @Override
    public List<T> listarTodosIncluindoInativos() {
        return jdbc.query(getSelectSql() + " ORDER BY id DESC", getRowMapper());
    }

    @Override
    public void deletar(Long id) {
        jdbc.update("UPDATE " + getTableName() + " SET ativo = false WHERE id = ?", id);
    }

    @Override
    public void toggleStatus(Long id) {
        jdbc.update("UPDATE " + getTableName() + " SET ativo = NOT ativo WHERE id = ?", id);
    }
}

