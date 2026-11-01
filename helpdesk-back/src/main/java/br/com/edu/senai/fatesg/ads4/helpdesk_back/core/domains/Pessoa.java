package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class Pessoa extends BaseModel {

    private String nome;
    private String email;
    private String telefone;
    private String setor;

    protected Pessoa(Long id, String nome, String email, String telefone, String setor, boolean ativo) {
        super(id, ativo);
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.setor = setor;
    }
}

