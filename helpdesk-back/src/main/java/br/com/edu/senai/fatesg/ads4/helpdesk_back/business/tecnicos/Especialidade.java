package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Especialidade {

    private String codigo;
    private String nome;
    private String nivelAtendimento;
}

