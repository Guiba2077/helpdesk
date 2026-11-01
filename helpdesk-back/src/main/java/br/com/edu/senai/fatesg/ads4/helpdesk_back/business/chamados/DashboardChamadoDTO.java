package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardChamadoDTO {

    private long totalChamados;
    private long chamadosAbertos;
    private long chamadosEmAtendimento;
    private long chamadosFechados;
    private long chamadosPrioridadeCritica;
}

