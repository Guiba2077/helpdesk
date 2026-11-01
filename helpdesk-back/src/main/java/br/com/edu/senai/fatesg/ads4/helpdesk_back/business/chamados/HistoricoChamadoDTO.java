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
public class HistoricoChamadoDTO {

    private Long id;
    private Long chamadoId;
    private String statusAnterior;
    private String statusNovo;
    private Long usuarioResponsavelId;
    private String usuarioResponsavelNome;
    private String observacao;
    private LocalDateTime dataMudanca;
}

