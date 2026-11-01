package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.helpers.IGenericMapper;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper implements IGenericMapper<UsuarioModel, UsuarioDTO> {

    @Override
    public UsuarioDTO toDto(UsuarioModel model) {
        if (model == null) {
            return null;
        }
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(model.getId());
        dto.setAtivo(model.isAtivo());
        dto.setNome(model.getNome());
        dto.setEmail(model.getEmail());
        dto.setTelefone(model.getTelefone());
        dto.setSetor(model.getSetor());
        dto.setPerfil(model.getPerfil());
        dto.setDataCadastro(model.getDataCadastro());
        return dto;
    }

    @Override
    public UsuarioModel toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }
        UsuarioModel model = new UsuarioModel();
        model.setId(dto.getId());
        model.setAtivo(dto.isAtivo());
        model.setNome(dto.getNome());
        model.setEmail(dto.getEmail());
        model.setTelefone(dto.getTelefone());
        model.setSetor(dto.getSetor());
        model.setPerfil(dto.getPerfil());
        model.setDataCadastro(dto.getDataCadastro());
        return model;
    }
}

