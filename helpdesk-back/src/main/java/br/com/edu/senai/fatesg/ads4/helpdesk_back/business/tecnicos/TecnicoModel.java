package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@Table(schema = "app", name = "tecnico")
public class TecnicoModel extends BaseModel {

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Column(name = "matricula", nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(name = "especialidade", nullable = false, length = 60)
    private String especialidade;

    @Column(name = "nivel_atendimento", nullable = false, length = 10)
    private String nivelAtendimento;

    @Builder.Default
    @Column(name = "disponivel", nullable = false)
    private boolean disponivel = true;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Builder.Default
    @Column(name = "criado_em", nullable = false)
    private java.time.LocalDateTime criadoEm = java.time.LocalDateTime.now();

    // Campos preenchidos via JOIN com usuario
    @Transient
    private String nome;

    @Transient
    private String email;

    @Transient
    private String telefone;

    @Transient
    private String setor;
}

