package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IGenericRepository<T extends BaseModel> extends Repository<T> {
}

