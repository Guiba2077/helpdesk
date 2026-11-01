package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApontamentoAtendimentoDTO {

    private Long id;
    private Long chamadoId;
    private Long autorId;
    private String autorNome;
    private String descricao;
    private LocalDateTime criadoEm;
}

