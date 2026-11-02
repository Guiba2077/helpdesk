package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * Trilha de auditoria de prioridade, lida pela view audit.vw_auditoria_prioridade.
 * Os registros sao gravados exclusivamente pela trigger trg_auditoria_prioridade
 * no PostgreSQL; a aplicacao apenas consulta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Immutable
@Table(schema = "audit", name = "vw_auditoria_prioridade")
public class AuditoriaPrioridadeModel extends BaseModel {

    @Column(name = "chamado_id", nullable = false)
    private Long chamadoId;

    @Column(name = "protocolo_chamado", nullable = false, length = 30)
    private String protocoloChamado;

    @Column(name = "prioridade_anterior", nullable = false, length = 20)
    private String prioridadeAnterior;

    @Column(name = "prioridade_nova", nullable = false, length = 20)
    private String prioridadeNova;

    @Column(name = "alterado_por")
    private Long alteradoPorId;

    @Column(name = "justificativa", columnDefinition = "TEXT")
    private String justificativa;

    @Builder.Default
    @Column(name = "alterado_em", nullable = false)
    private LocalDateTime dataAlteracao = LocalDateTime.now();

    @Column(name = "titulo_chamado")
    private String tituloChamado;

    @Column(name = "alterado_por_nome")
    private String alteradoPorNome;
}
