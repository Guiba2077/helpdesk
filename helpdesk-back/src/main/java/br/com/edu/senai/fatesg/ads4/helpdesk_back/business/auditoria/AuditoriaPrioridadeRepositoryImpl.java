package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.NegocioException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories.AbstractRepository;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Repositório de leitura da trilha de auditoria no schema "audit".
 * Utiliza o leituraJdbcTemplate (conectado com usuário da role rl_leitor)
 * e consome a View 2 ativa: audit.vw_auditoria_prioridade.
 */
@Repository("businessAuditoriaPrioridadeRepository")
public class AuditoriaPrioridadeRepositoryImpl extends AbstractRepository<AuditoriaPrioridadeModel>
        implements IAuditoriaPrioridadeRepository {

    public AuditoriaPrioridadeRepositoryImpl(@Qualifier("leituraJdbcTemplate") JdbcTemplate leituraJdbc) {
        super(leituraJdbc);
    }

    @Override
    protected String getTableName() {
        return "audit.auditoria_prioridade";
    }

    @Override
    protected String getSelectSql() {
        // Consome a View 2 ativa exigida pelo edital: audit.vw_auditoria_prioridade
        return "SELECT * FROM audit.vw_auditoria_prioridade";
    }

    @Override
    protected RowMapper<AuditoriaPrioridadeModel> getRowMapper() {
        return (rs, rowNum) -> {
            AuditoriaPrioridadeModel model = new AuditoriaPrioridadeModel();
            model.setId(rs.getLong("id"));
            model.setChamadoId(rs.getLong("chamado_id"));
            model.setProtocoloChamado(rs.getString("protocolo_chamado"));
            model.setTituloChamado(rs.getString("titulo_chamado"));
            model.setPrioridadeAnterior(rs.getString("prioridade_anterior"));
            model.setPrioridadeNova(rs.getString("prioridade_nova"));
            long altPor = rs.getLong("alterado_por");
            if (!rs.wasNull()) {
                model.setAlteradoPorId(altPor);
            }
            model.setAlteradoPorNome(rs.getString("alterado_por_nome"));
            model.setJustificativa(rs.getString("justificativa"));
            Timestamp ts = rs.getTimestamp("alterado_em");
            if (ts != null) {
                model.setDataAlteracao(ts.toLocalDateTime());
                model.setDataCadastro(ts.toLocalDateTime());
            }
            model.setAtivo(true);
            return model;
        };
    }

    @Override
    public List<AuditoriaPrioridadeModel> listarTodos() {
        return jdbc.query(getSelectSql() + " ORDER BY alterado_em DESC, id DESC", getRowMapper());
    }

    @Override
    public List<AuditoriaPrioridadeModel> listarTodosIncluindoInativos() {
        return listarTodos();
    }

    @Override
    public List<AuditoriaPrioridadeModel> listarPorChamado(Long chamadoId) {
        return jdbc.query(
                getSelectSql() + " WHERE chamado_id = ? ORDER BY alterado_em DESC, id DESC",
                getRowMapper(),
                chamadoId);
    }

    @Override
    public AuditoriaPrioridadeModel salvar(AuditoriaPrioridadeModel entity) {
        throw new NegocioException(
                "A tabela audit.auditoria_prioridade é imutável pela aplicação e alimentada exclusivamente por Trigger no PostgreSQL.");
    }

    @Override
    public void atualizar(AuditoriaPrioridadeModel entity) {
        throw new NegocioException("Registros de auditoria não podem ser alterados.");
    }

    @Override
    public void deletar(Long id) {
        throw new NegocioException("Registros de auditoria não podem ser excluídos.");
    }

    @Override
    public void toggleStatus(Long id) {
        throw new NegocioException("Registros de auditoria são imutáveis.");
    }
}

