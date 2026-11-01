package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.GenericService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("businessAuditoriaPrioridadeService")
public class AuditoriaPrioridadeService
        extends GenericService<AuditoriaPrioridadeModel, AuditoriaPrioridadeDTO, IAuditoriaPrioridadeRepository>
        implements IAuditoriaPrioridadeService {

    private final AuditoriaPrioridadeMapper mapper;

    public AuditoriaPrioridadeService(
            IAuditoriaPrioridadeRepository repository,
            AuditoriaPrioridadeMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected AuditoriaPrioridadeDTO toDTO(AuditoriaPrioridadeModel model) {
        return mapper.toDto(model);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaPrioridadeDTO> listarPorChamado(Long chamadoId) {
        return repository.listarPorChamado(chamadoId).stream().map(mapper::toDto).toList();
    }
}

