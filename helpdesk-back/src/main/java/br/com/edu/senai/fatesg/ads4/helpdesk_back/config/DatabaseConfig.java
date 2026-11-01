package br.com.edu.senai.fatesg.ads4.helpdesk_back.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuração dos DataSources e JdbcTemplates separando fisicamente:
 * 1) Conexão principal de escrita/operacional (usuário membro da role rl_escritor).
 * 2) Conexão exclusiva de leitura para Auditoria e Relatórios (usuário membro da role rl_leitor).
 * Ambos compatíveis com PgBouncer (prepareThreshold=0).
 */
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public HikariDataSource primaryDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.addDataSourceProperty("prepareThreshold", "0");
        return ds;
    }

    @Bean(name = "leituraDataSource")
    @ConfigurationProperties("helpdesk.datasource.leitura")
    public HikariDataSource leituraDataSource(
            @Qualifier("primaryDataSource") HikariDataSource primary) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(primary.getJdbcUrl());
        ds.setDriverClassName(primary.getDriverClassName());
        ds.setUsername(primary.getUsername());
        ds.setPassword(primary.getPassword());
        ds.setMaximumPoolSize(5);
        ds.addDataSourceProperty("prepareThreshold", "0");
        return ds;
    }

    @Bean(name = "jdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "leituraJdbcTemplate")
    public JdbcTemplate leituraJdbcTemplate(@Qualifier("leituraDataSource") DataSource leituraDataSource) {
        return new JdbcTemplate(leituraDataSource);
    }
}

