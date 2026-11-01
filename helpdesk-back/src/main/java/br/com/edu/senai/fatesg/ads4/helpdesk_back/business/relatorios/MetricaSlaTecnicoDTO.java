package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.relatorios;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricaSlaTecnicoDTO {

    private Long tecnicoId;
    private String tecnicoNome;
    private String especialidade;
    private long totalChamadosAtribuidos;
    private long chamadosEncerrados;
    private long chamadosEmAtendimento;
    private BigDecimal tempoMedioResolucaoHoras;
}

