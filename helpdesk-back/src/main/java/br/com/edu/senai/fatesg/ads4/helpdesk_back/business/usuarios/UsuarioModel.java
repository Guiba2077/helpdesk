package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
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
public class UsuarioModel extends BaseModel {

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String setor;
    private String perfil;
}

