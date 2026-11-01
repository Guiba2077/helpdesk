package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import java.util.List;
import java.util.Optional;

public interface Repository<T extends BaseModel> {

    Optional<T> buscarPorId(Long id);

    List<T> listarTodos();

    List<T> listarTodosIncluindoInativos();

    T salvar(T entity);

    void atualizar(T entity);

    void deletar(Long id);

    void toggleStatus(Long id);
}

