package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "app", name = "historico_chamado")
public class HistoricoChamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chamado_id", nullable = false)
    private Long chamadoId;

    @Column(name = "status_anterior", length = 20)
    private String statusAnterior;

    @Column(name = "status_novo", nullable = false, length = 20)
    private String statusNovo;

    @Column(name = "alterado_por")
    private Long usuarioResponsavelId;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Builder.Default
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataMudanca = LocalDateTime.now();

    @Transient
    private String usuarioResponsavelNome;
}

