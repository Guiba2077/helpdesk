package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.helpers.IGenericMapper;
import org.springframework.stereotype.Component;

@Component
public class ChamadoMapper implements IGenericMapper<ChamadoModel, ChamadoDTO> {

    @Override
    public ChamadoDTO toDto(ChamadoModel model) {
        if (model == null) {
            return null;
        }
        ChamadoDTO dto = new ChamadoDTO();
        dto.setId(model.getId());
        dto.setAtivo(model.isAtivo());
        dto.setProtocolo(model.getProtocolo());
        dto.setTitulo(model.getTitulo());
        dto.setDescricao(model.getDescricao());
        dto.setCategoria(model.getCategoria());
        dto.setPrioridade(model.getPrioridade());
        dto.setStatus(model.getStatus());
        dto.setSolicitanteId(model.getSolicitanteId());
        dto.setSolicitanteNome(model.getSolicitanteNome());
        dto.setSolicitanteEmail(model.getSolicitanteEmail());
        dto.setSolicitanteSetor(model.getSolicitanteSetor());
        dto.setTecnicoId(model.getTecnicoId());
        dto.setTecnicoNome(model.getTecnicoNome());
        dto.setTecnicoEspecialidade(model.getTecnicoEspecialidade());
        dto.setSolucao(model.getSolucao());
        dto.setDataAbertura(model.getDataAbertura());
        dto.setDataAtendimento(model.getDataAtendimento());
        dto.setDataFechamento(model.getDataFechamento());
        return dto;
    }

    @Override
    public ChamadoModel toEntity(ChamadoDTO dto) {
        if (dto == null) {
            return null;
        }
        ChamadoModel model = new ChamadoModel();
        model.setId(dto.getId());
        model.setAtivo(dto.isAtivo());
        model.setProtocolo(dto.getProtocolo());
        model.setTitulo(dto.getTitulo());
        model.setDescricao(dto.getDescricao());
        model.setCategoria(dto.getCategoria());
        model.setPrioridade(dto.getPrioridade());
        model.setStatus(dto.getStatus());
        model.setSolicitanteId(dto.getSolicitanteId());
        model.setSolicitanteNome(dto.getSolicitanteNome());
        model.setSolicitanteEmail(dto.getSolicitanteEmail());
        model.setSolicitanteSetor(dto.getSolicitanteSetor());
        model.setTecnicoId(dto.getTecnicoId());
        model.setTecnicoNome(dto.getTecnicoNome());
        model.setTecnicoEspecialidade(dto.getTecnicoEspecialidade());
        model.setSolucao(dto.getSolucao());
        model.setDataAbertura(dto.getDataAbertura());
        model.setDataAtendimento(dto.getDataAtendimento());
        model.setDataFechamento(dto.getDataFechamento());
        return model;
    }

    public HistoricoChamadoDTO toHistoricoDto(HistoricoChamado h) {
        if (h == null) {
            return null;
        }
        return HistoricoChamadoDTO.builder()
                .id(h.getId())
                .chamadoId(h.getChamadoId())
                .statusAnterior(h.getStatusAnterior())
                .statusNovo(h.getStatusNovo())
                .usuarioResponsavelId(h.getUsuarioResponsavelId())
                .usuarioResponsavelNome(h.getUsuarioResponsavelNome())
                .observacao(h.getObservacao())
                .dataMudanca(h.getDataMudanca())
                .build();
    }

    public ApontamentoAtendimentoDTO toApontamentoDto(ApontamentoAtendimento a) {
        if (a == null) {
            return null;
        }
        return ApontamentoAtendimentoDTO.builder()
                .id(a.getId())
                .chamadoId(a.getChamadoId())
                .autorId(a.getAutorId())
                .autorNome(a.getAutorNome())
                .descricao(a.getDescricao())
                .criadoEm(a.getCriadoEm())
                .build();
    }
}

