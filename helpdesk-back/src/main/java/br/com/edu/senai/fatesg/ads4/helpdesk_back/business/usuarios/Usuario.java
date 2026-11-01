package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.Pessoa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Usuario extends Pessoa {

    private PerfilUsuario perfil;

    public Usuario(Long id, String nome, String email, String telefone, String setor, PerfilUsuario perfil, boolean ativo) {
        super(id, nome, email, telefone, setor, ativo);
        this.perfil = perfil;
    }

    public boolean isAdmin() {
        return this.perfil == PerfilUsuario.ADMIN;
    }

    public boolean isTecnico() {
        return this.perfil == PerfilUsuario.TECNICO;
    }
}

