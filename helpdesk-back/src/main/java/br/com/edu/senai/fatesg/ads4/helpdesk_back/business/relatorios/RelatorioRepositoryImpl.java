package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.relatorios;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repositório de relatórios que utiliza o leituraJdbcTemplate (role rl_leitor):
 * 1) Consome a Function obrigatória do PostgreSQL: app.fn_metricas_sla_tecnico()
 * 2) Permite demonstrar na apresentação o cancelamento automático de relatórios pesados pelo statement_timeout curto.
 */
@Repository("businessRelatorioRepository")
public class RelatorioRepositoryImpl implements IRelatorioRepository {

    private final JdbcTemplate leituraJdbc;

    public RelatorioRepositoryImpl(@Qualifier("leituraJdbcTemplate") JdbcTemplate leituraJdbc) {
        this.leituraJdbc = leituraJdbc;
    }

    @Override
    public List<MetricaSlaTecnicoDTO> consultarMetricasSlaTecnicos() {
        String sql = "SELECT * FROM app.fn_metricas_sla_tecnico()";
        return leituraJdbc.query(sql, (rs, rowNum) -> MetricaSlaTecnicoDTO.builder()
                .tecnicoId(rs.getLong("tecnico_id"))
                .tecnicoNome(rs.getString("tecnico_nome"))
                .especialidade(rs.getString("especialidade"))
                .totalChamadosAtribuidos(rs.getLong("total_atribuidos"))
                .chamadosEncerrados(rs.getLong("total_fechados"))
                .chamadosEmAtendimento(rs.getLong("total_em_atendimento"))
                .tempoMedioResolucaoHoras(
                        rs.getBigDecimal("tempo_medio_horas") != null
                                ? rs.getBigDecimal("tempo_medio_horas")
                                : BigDecimal.ZERO)
                .build());
    }

    @Override
    public void executarRelatorioPesadoSimulandoTimeout(int segundosEspera) {
        // Executa uma query que ultrapassa o statement_timeout curto (ex: 3s no postgresql.conf)
        leituraJdbc.queryForObject("SELECT pg_sleep(?)::text", String.class, segundosEspera);
    }
}

