package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.dtos.BaseDTO;
import java.util.List;

public interface IGenericService<D extends BaseDTO> {

    List<D> listarTodos();

    List<D> listarTodos(boolean incluirInativos);

    D buscarPorId(Long id);

    void toggleStatus(Long id);

    void deletar(Long id);
}

