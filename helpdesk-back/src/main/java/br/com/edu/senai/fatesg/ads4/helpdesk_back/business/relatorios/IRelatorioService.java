package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.relatorios;

import java.util.List;

public interface IRelatorioService {

    List<MetricaSlaTecnicoDTO> obterMetricasSlaTecnicos();

    String dispararRelatorioPesado(int segundos);
}

