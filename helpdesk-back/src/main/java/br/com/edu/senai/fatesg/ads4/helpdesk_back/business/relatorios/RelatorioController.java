package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.relatorios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers.response.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final IRelatorioService service;

    public RelatorioController(IRelatorioService service) {
        this.service = service;
    }

    @GetMapping("/sla-tecnicos")
    public ResponseEntity<List<MetricaSlaTecnicoDTO>> obterMetricasSlaTecnicos() {
        return ResponseEntity.ok(service.obterMetricasSlaTecnicos());
    }

    @GetMapping("/pesado")
    public ResponseEntity<ApiResponse<String>> executarRelatorioPesado(
            @RequestParam(name = "segundos", defaultValue = "6") int segundos) {
        String resultado = service.dispararRelatorioPesado(segundos);
        return ResponseEntity.ok(ApiResponse.ok(resultado, "Consulta executada com sucesso."));
    }
}

