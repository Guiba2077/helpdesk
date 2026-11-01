package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.patterns;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Gerenciador de contexto de sessão do PostgreSQL.
 * Propaga o usuário logado e seu perfil para a transação atual no SGBD via set_config(..., true),
 * permitindo que as Triggers (histórico de status e auditoria de prioridade) e políticas de
 * Row-Level Security (RLS) saibam quem está executando a operação mesmo com PgBouncer em modo transaction.
 */
@Component
public class SessionContextManager {

    private final JdbcTemplate jdbcTemplate;

    public SessionContextManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void definirContextoUsuario(Long usuarioId, String perfil) {
        Long idEfetivo = (usuarioId != null && usuarioId > 0) ? usuarioId : 1L;
        String perfilEfetivo = (perfil != null && !perfil.isBlank()) ? perfil : "ADMIN";

        jdbcTemplate.queryForObject(
                "SELECT set_config('app.current_user_id', ?, true)",
                String.class,
                String.valueOf(idEfetivo));
        jdbcTemplate.queryForObject(
                "SELECT set_config('app.current_user_role', ?, true)",
                String.class,
                perfilEfetivo);
    }

    public void definirUsuarioSessao(Long usuarioId) {
        definirContextoUsuario(usuarioId, "ADMIN");
    }
}

