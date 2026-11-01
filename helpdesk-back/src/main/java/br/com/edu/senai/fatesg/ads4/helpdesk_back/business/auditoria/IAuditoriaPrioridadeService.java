package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import java.util.List;

public interface IAuditoriaPrioridadeService extends IGenericService<AuditoriaPrioridadeDTO> {

    List<AuditoriaPrioridadeDTO> listarPorChamado(Long chamadoId);
}

