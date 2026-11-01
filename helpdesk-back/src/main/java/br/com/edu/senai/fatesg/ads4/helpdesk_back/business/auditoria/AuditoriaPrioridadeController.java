package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers.GenericController;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria/prioridades")
public class AuditoriaPrioridadeController extends GenericController<AuditoriaPrioridadeDTO> {

    private final IAuditoriaPrioridadeService service;

    public AuditoriaPrioridadeController(IAuditoriaPrioridadeService service) {
        this.service = service;
    }

    @Override
    protected IGenericService<AuditoriaPrioridadeDTO> getService() {
        return service;
    }

    @GetMapping("/chamado/{chamadoId}")
    public ResponseEntity<List<AuditoriaPrioridadeDTO>> listarPorChamado(@PathVariable Long chamadoId) {
        return ResponseEntity.ok(service.listarPorChamado(chamadoId));
    }
}

