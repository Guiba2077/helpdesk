package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

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

/**
 * Repositório JDBC do Agregado Central (Chamado).
 * Integra com:
 * - View 1: app.vw_chamados_detalhados
 * - Procedure: CALL app.sp_alocar_chamado(?, ?, ?)
 * - Trigger 1: trg_historico_status (Aberto -> Atendimento -> Fechado) em app.historico_chamado
 * - Trigger 2: trg_auditoria_prioridade em audit.auditoria_prioridade
 */
@Repository("businessChamadoRepository")
public class ChamadoRepositoryImpl extends AbstractRepository<ChamadoModel> implements IChamadoRepository {

    public ChamadoRepositoryImpl(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected String getTableName() {
        return "app.chamado";
    }

    @Override
    protected String getSelectSql() {
        // Consome a View 1 ativa exigida no edital: app.vw_chamados_detalhados
        return "SELECT * FROM app.vw_chamados_detalhados";
    }

    @Override
    protected RowMapper<ChamadoModel> getRowMapper() {
        return (rs, rowNum) -> {
            ChamadoModel model = new ChamadoModel();
            model.setId(rs.getLong("id"));
            model.setProtocolo(rs.getString("protocolo"));
            model.setTitulo(rs.getString("titulo"));
            model.setDescricao(rs.getString("descricao"));
            model.setCategoria(rs.getString("categoria"));
            model.setPrioridade(rs.getString("prioridade"));
            model.setStatus(rs.getString("status"));
            model.setSolicitanteId(rs.getLong("solicitante_id"));
            model.setSolicitanteNome(rs.getString("solicitante_nome"));
            model.setSolicitanteEmail(rs.getString("solicitante_email"));
            model.setSolicitanteSetor(rs.getString("solicitante_setor"));

            long tecId = rs.getLong("tecnico_id");
            if (!rs.wasNull()) {
                model.setTecnicoId(tecId);
            }
            model.setTecnicoNome(rs.getString("tecnico_nome"));
            model.setTecnicoEspecialidade(rs.getString("tecnico_especialidade"));
            model.setSolucao(rs.getString("solucao"));
            model.setAtivo(rs.getBoolean("ativo"));

            Timestamp abertoEm = rs.getTimestamp("aberto_em");
            if (abertoEm != null) {
                model.setDataAbertura(abertoEm.toLocalDateTime());
                model.setDataCadastro(abertoEm.toLocalDateTime());
            }
            Timestamp atendimentoEm = rs.getTimestamp("atendimento_em");
            if (atendimentoEm != null) {
                model.setDataAtendimento(atendimentoEm.toLocalDateTime());
            }
            Timestamp fechadoEm = rs.getTimestamp("fechado_em");
            if (fechadoEm != null) {
                model.setDataFechamento(fechadoEm.toLocalDateTime());
            }
            return model;
        };
    }

    @Override
    public Optional<ChamadoModel> buscarPorId(Long id) {
        List<ChamadoModel> lista = jdbc.query(getSelectSql() + " WHERE id = ?", getRowMapper(), id);
        return lista.stream().findFirst();
    }

    @Override
    public List<ChamadoModel> listarTodos() {
        return jdbc.query(getSelectSql() + " WHERE ativo = true ORDER BY aberto_em DESC, id DESC", getRowMapper());
    }

    @Override
    public List<ChamadoModel> listarTodosIncluindoInativos() {
        return jdbc.query(getSelectSql() + " ORDER BY aberto_em DESC, id DESC", getRowMapper());
    }

    @Override
    public List<ChamadoModel> buscarPorStatus(String status) {
        String sql = getSelectSql() + " WHERE UPPER(status) = UPPER(?) AND ativo = true ORDER BY aberto_em DESC";
        return jdbc.query(sql, getRowMapper(), status);
    }

    @Override
    public ChamadoModel salvar(ChamadoModel entity) {
        String sql = """
                INSERT INTO app.chamado
                    (titulo, descricao, categoria, prioridade, status, solicitante_id, ativo)
                VALUES (?, ?, ?, ?, 'ABERTO', ?, true)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, entity.getTitulo());
            ps.setString(2, entity.getDescricao());
            ps.setString(3, entity.getCategoria() != null ? entity.getCategoria().toUpperCase() : "SOFTWARE");
            ps.setString(4, entity.getPrioridade() != null ? entity.getPrioridade().toUpperCase() : "MEDIA");
            ps.setLong(5, entity.getSolicitanteId());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            entity.setId(generatedId.longValue());
        }
        return buscarPorId(entity.getId()).orElse(entity);
    }

    @Override
    public void atualizar(ChamadoModel entity) {
        String sql = """
                UPDATE app.chamado
                   SET titulo = ?,
                       descricao = ?,
                       categoria = ?,
                       atualizado_em = now()
                 WHERE id = ?
                """;
        jdbc.update(sql,
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getCategoria() != null ? entity.getCategoria().toUpperCase() : "SOFTWARE",
                entity.getId());
    }

    @Override
    public void alocarTecnicoViaProcedure(Long chamadoId, Long tecnicoId, Long usuarioResponsavelId) {
        // Chama a Procedure obrigatória do SGBD: app.sp_alocar_chamado
        jdbc.update("CALL app.sp_alocar_chamado(?, ?, ?)", chamadoId, tecnicoId, usuarioResponsavelId);
    }

    @Override
    public void atualizarPrioridade(Long chamadoId, String novaPrioridade, String justificativa) {
        if (justificativa != null && !justificativa.isBlank()) {
            jdbc.queryForObject(
                    "SELECT set_config('app.motivo_prioridade', ?, true)",
                    String.class,
                    justificativa.trim());
        }
        // O UPDATE na coluna prioridade dispara a Trigger trg_auditoria_prioridade no schema audit
        String sql = """
                UPDATE app.chamado
                   SET prioridade = ?,
                       atualizado_em = now()
                 WHERE id = ?
                """;
        jdbc.update(sql, novaPrioridade.toUpperCase(), chamadoId);
    }

    @Override
    public void encerrarChamado(Long chamadoId, String solucao) {
        // O UPDATE na coluna status para FECHADO dispara a Trigger trg_historico_status
        String sql = """
                UPDATE app.chamado
                   SET status = 'FECHADO',
                       solucao = ?,
                       fechado_em = now(),
                       atualizado_em = now()
                 WHERE id = ?
                """;
        jdbc.update(sql, solucao, chamadoId);
    }

    @Override
    public List<HistoricoChamado> listarHistoricoPorChamado(Long chamadoId) {
        String sql = """
                SELECT h.id,
                       h.chamado_id,
                       h.status_anterior,
                       h.status_novo,
                       h.alterado_por,
                       COALESCE(u.nome, 'Sistema / Trigger') AS usuario_nome,
                       h.observacao,
                       h.criado_em
                  FROM app.historico_chamado h
                  LEFT JOIN app.usuario u ON u.id = h.alterado_por
                 WHERE h.chamado_id = ?
                 ORDER BY h.criado_em ASC, h.id ASC
                """;
        return jdbc.query(sql, (rs, rowNum) -> {
            Long alteradoPor = rs.getLong("alterado_por");
            if (rs.wasNull()) {
                alteradoPor = null;
            }
            Timestamp ts = rs.getTimestamp("criado_em");
            return HistoricoChamado.builder()
                    .id(rs.getLong("id"))
                    .chamadoId(rs.getLong("chamado_id"))
                    .statusAnterior(rs.getString("status_anterior"))
                    .statusNovo(rs.getString("status_novo"))
                    .usuarioResponsavelId(alteradoPor)
                    .usuarioResponsavelNome(rs.getString("usuario_nome"))
                    .observacao(rs.getString("observacao"))
                    .dataMudanca(ts != null ? ts.toLocalDateTime() : null)
                    .build();
        }, chamadoId);
    }

    @Override
    public ApontamentoAtendimento adicionarApontamento(Long chamadoId, Long autorId, String descricao) {
        String sql = """
                INSERT INTO app.apontamento_chamado (chamado_id, autor_id, mensagem)
                VALUES (?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, chamadoId);
            ps.setLong(2, autorId);
            ps.setString(3, descricao);
            return ps;
        }, keyHolder);

        Long idGerado = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        return listarApontamentosPorChamado(chamadoId).stream()
                .filter(a -> a.getId().equals(idGerado))
                .findFirst()
                .orElse(ApontamentoAtendimento.builder()
                        .id(idGerado)
                        .chamadoId(chamadoId)
                        .autorId(autorId)
                        .descricao(descricao)
                        .build());
    }

    @Override
    public List<ApontamentoAtendimento> listarApontamentosPorChamado(Long chamadoId) {
        String sql = """
                SELECT a.id,
                       a.chamado_id,
                       a.autor_id,
                       u.nome AS autor_nome,
                       a.mensagem,
                       a.criado_em
                  FROM app.apontamento_chamado a
                  JOIN app.usuario u ON u.id = a.autor_id
                 WHERE a.chamado_id = ?
                 ORDER BY a.criado_em ASC, a.id ASC
                """;
        return jdbc.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("criado_em");
            return ApontamentoAtendimento.builder()
                    .id(rs.getLong("id"))
                    .chamadoId(rs.getLong("chamado_id"))
                    .autorId(rs.getLong("autor_id"))
                    .autorNome(rs.getString("autor_nome"))
                    .descricao(rs.getString("mensagem"))
                    .criadoEm(ts != null ? ts.toLocalDateTime() : null)
                    .build();
        }, chamadoId);
    }

    @Override
    public DashboardChamadoDTO obterResumoDashboard() {
        String sql = """
                SELECT COUNT(*) AS total,
                       COUNT(*) FILTER (WHERE status = 'ABERTO') AS abertos,
                       COUNT(*) FILTER (WHERE status = 'ATENDIMENTO') AS em_atendimento,
                       COUNT(*) FILTER (WHERE status = 'FECHADO') AS fechados,
                       COUNT(*) FILTER (WHERE prioridade = 'CRITICA' AND status <> 'FECHADO') AS criticos
                  FROM app.chamado
                 WHERE ativo = true
                """;
        return jdbc.queryForObject(sql, (rs, rowNum) -> DashboardChamadoDTO.builder()
                .totalChamados(rs.getLong("total"))
                .chamadosAbertos(rs.getLong("abertos"))
                .chamadosEmAtendimento(rs.getLong("em_atendimento"))
                .chamadosFechados(rs.getLong("fechados"))
                .chamadosPrioridadeCritica(rs.getLong("criticos"))
                .build());
    }
}

