package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.helpers;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.dtos.BaseDTO;

public interface IGenericMapper<T extends BaseModel, D extends BaseDTO> {

    D toDto(T model);

    T toEntity(D dto);
}

