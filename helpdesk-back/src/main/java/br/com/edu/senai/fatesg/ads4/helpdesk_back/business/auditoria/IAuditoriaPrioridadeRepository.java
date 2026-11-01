package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories.Repository;
import java.util.List;

public interface IAuditoriaPrioridadeRepository extends Repository<AuditoriaPrioridadeModel> {

    List<AuditoriaPrioridadeModel> listarPorChamado(Long chamadoId);
}

