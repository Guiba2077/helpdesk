package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.relatorios;

import java.util.List;

public interface IRelatorioRepository {

    List<MetricaSlaTecnicoDTO> consultarMetricasSlaTecnicos();

    void executarRelatorioPesadoSimulandoTimeout(int segundosEspera);
}

