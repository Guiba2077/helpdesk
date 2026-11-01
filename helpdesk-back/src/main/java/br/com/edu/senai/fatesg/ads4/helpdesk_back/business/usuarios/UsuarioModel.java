package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(schema = "app", name = "usuario")
public class UsuarioModel extends BaseModel {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senha;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Column(name = "setor", nullable = false, length = 80)
    private String setor;

    @Column(name = "perfil", nullable = false, length = 20)
    private String perfil;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private java.time.LocalDateTime criadoEm = java.time.LocalDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private java.time.LocalDateTime atualizadoEm = java.time.LocalDateTime.now();
}

