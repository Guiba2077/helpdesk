package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "app", name = "chamado")
public class ChamadoModel extends BaseModel {

    @Column(name = "protocolo", nullable = false, unique = true, length = 30, insertable = false)
    private String protocolo;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "categoria", nullable = false, length = 40)
    private String categoria;

    @Column(name = "prioridade", nullable = false, length = 20)
    private String prioridade;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "solicitante_id", nullable = false)
    private Long solicitanteId;

    @Column(name = "tecnico_id")
    private Long tecnicoId;

    @Column(name = "solucao", columnDefinition = "TEXT")
    private String solucao;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Builder.Default
    @Column(name = "aberto_em", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "atendimento_em")
    private LocalDateTime dataAtendimento;

    @Column(name = "fechado_em")
    private LocalDateTime dataFechamento;

    @Builder.Default
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    // Campos enriquecidos para exibição vindos da view ou joins
    @Transient
    private String solicitanteNome;

    @Transient
    private String solicitanteEmail;

    @Transient
    private String solicitanteSetor;

    @Transient
    private String tecnicoNome;

    @Transient
    private String tecnicoEspecialidade;
}

