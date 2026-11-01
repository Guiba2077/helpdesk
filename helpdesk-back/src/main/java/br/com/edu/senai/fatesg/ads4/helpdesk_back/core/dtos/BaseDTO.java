package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {

    private Long id;
    private boolean ativo = true;
}

