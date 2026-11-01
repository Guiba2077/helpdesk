package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.relatorios;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("businessRelatorioService")
public class RelatorioService implements IRelatorioService {

    private final IRelatorioRepository repository;

    public RelatorioService(IRelatorioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricaSlaTecnicoDTO> obterMetricasSlaTecnicos() {
        return repository.consultarMetricasSlaTecnicos();
    }

    @Override
    @Transactional(readOnly = true)
    public String dispararRelatorioPesado(int segundos) {
        int tempo = segundos > 0 ? segundos : 6;
        repository.executarRelatorioPesadoSimulandoTimeout(tempo);
        return "Relatório pesado concluído em " + tempo + "s (statement_timeout não foi excedido).";
    }
}

