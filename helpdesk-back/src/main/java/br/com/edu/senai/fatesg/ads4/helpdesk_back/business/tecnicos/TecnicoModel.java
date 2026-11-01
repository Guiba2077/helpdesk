package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

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
public class TecnicoModel extends BaseModel {

    private Long usuarioId;
    private String nome;
    private String email;
    private String telefone;
    private String setor;
    private String matricula;
    private String especialidade;
    private String nivelAtendimento;
    private boolean disponivel;
}

