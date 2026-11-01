package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel {

    private Long id;
    private LocalDateTime dataCadastro = LocalDateTime.now();
    private boolean ativo = true;

    protected BaseModel(Long id, boolean ativo) {
        this.id = id;
        this.ativo = ativo;
        this.dataCadastro = LocalDateTime.now();
    }
}

