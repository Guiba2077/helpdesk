package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.helpers.IGenericMapper;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaPrioridadeMapper implements IGenericMapper<AuditoriaPrioridadeModel, AuditoriaPrioridadeDTO> {

    @Override
    public AuditoriaPrioridadeDTO toDto(AuditoriaPrioridadeModel model) {
        if (model == null) {
            return null;
        }
        AuditoriaPrioridadeDTO dto = new AuditoriaPrioridadeDTO();
        dto.setId(model.getId());
        dto.setAtivo(true);
        dto.setChamadoId(model.getChamadoId());
        dto.setProtocoloChamado(model.getProtocoloChamado());
        dto.setTituloChamado(model.getTituloChamado());
        dto.setPrioridadeAnterior(model.getPrioridadeAnterior());
        dto.setPrioridadeNova(model.getPrioridadeNova());
        dto.setAlteradoPorId(model.getAlteradoPorId());
        dto.setAlteradoPorNome(model.getAlteradoPorNome());
        dto.setJustificativa(model.getJustificativa());
        dto.setDataAlteracao(model.getDataAlteracao());
        return dto;
    }

    @Override
    public AuditoriaPrioridadeModel toEntity(AuditoriaPrioridadeDTO dto) {
        if (dto == null) {
            return null;
        }
        AuditoriaPrioridadeModel model = new AuditoriaPrioridadeModel();
        model.setId(dto.getId());
        model.setAtivo(true);
        model.setChamadoId(dto.getChamadoId());
        model.setProtocoloChamado(dto.getProtocoloChamado());
        model.setTituloChamado(dto.getTituloChamado());
        model.setPrioridadeAnterior(dto.getPrioridadeAnterior());
        model.setPrioridadeNova(dto.getPrioridadeNova());
        model.setAlteradoPorId(dto.getAlteradoPorId());
        model.setAlteradoPorNome(dto.getAlteradoPorNome());
        model.setJustificativa(dto.getJustificativa());
        model.setDataAlteracao(dto.getDataAlteracao());
        return model;
    }
}

