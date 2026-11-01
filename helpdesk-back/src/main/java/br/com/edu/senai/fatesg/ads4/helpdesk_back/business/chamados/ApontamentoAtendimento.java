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
@Table(schema = "app", name = "apontamento_chamado")
public class ApontamentoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chamado_id", nullable = false)
    private Long chamadoId;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Builder.Default
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Transient
    private String autorNome;
}

