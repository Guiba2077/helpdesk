package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.helpers.IGenericMapper;
import org.springframework.stereotype.Component;

@Component
public class TecnicoMapper implements IGenericMapper<TecnicoModel, TecnicoDTO> {

    @Override
    public TecnicoDTO toDto(TecnicoModel model) {
        if (model == null) {
            return null;
        }
        TecnicoDTO dto = new TecnicoDTO();
        dto.setId(model.getId());
        dto.setAtivo(model.isAtivo());
        dto.setUsuarioId(model.getUsuarioId());
        dto.setNome(model.getNome());
        dto.setEmail(model.getEmail());
        dto.setTelefone(model.getTelefone());
        dto.setSetor(model.getSetor());
        dto.setMatricula(model.getMatricula());
        dto.setEspecialidade(model.getEspecialidade());
        dto.setNivelAtendimento(model.getNivelAtendimento());
        dto.setDisponivel(model.isDisponivel());
        return dto;
    }

    @Override
    public TecnicoModel toEntity(TecnicoDTO dto) {
        if (dto == null) {
            return null;
        }
        TecnicoModel model = new TecnicoModel();
        model.setId(dto.getId());
        model.setAtivo(dto.isAtivo());
        model.setUsuarioId(dto.getUsuarioId());
        model.setNome(dto.getNome());
        model.setEmail(dto.getEmail());
        model.setTelefone(dto.getTelefone());
        model.setSetor(dto.getSetor());
        model.setMatricula(dto.getMatricula());
        model.setEspecialidade(dto.getEspecialidade());
        model.setNivelAtendimento(dto.getNivelAtendimento());
        model.setDisponivel(dto.isDisponivel());
        return model;
    }
}

