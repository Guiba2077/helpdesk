package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.domains.BaseModel;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.dtos.BaseDTO;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.BusinessException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.EntidadeNaoEncontradaException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories.Repository;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public abstract class GenericService<T extends BaseModel, D extends BaseDTO, R extends Repository<T>>
        implements IGenericService<D> {

    protected final R repository;

    protected GenericService(R repository) {
        this.repository = repository;
    }

    protected abstract D toDTO(T model);

    @Override
    @Transactional(readOnly = true)
    public List<D> listarTodos() {
        return repository.listarTodos().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<D> listarTodos(boolean incluirInativos) {
        List<T> lista = incluirInativos
                ? repository.listarTodosIncluindoInativos()
                : repository.listarTodos();
        return lista.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public D buscarPorId(Long id) {
        T model = repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Registro não encontrado com ID: " + id));
        return toDTO(model);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        buscarPorId(id);
        repository.toggleStatus(id);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        repository.deletar(id);
    }

    protected <X> X executarComTratamentoDuplicidade(Supplier<X> acao) {
        try {
            return acao.get();
        } catch (DataIntegrityViolationException ex) {
            String msg = ex.getMostSpecificCause().getMessage() != null
                    ? ex.getMostSpecificCause().getMessage().toLowerCase()
                    : "";
            if (msg.contains("email")) {
                throw new BusinessException("Já existe um registro cadastrado com este e-mail.", ex);
            }
            if (msg.contains("matricula")) {
                throw new BusinessException("Já existe um técnico cadastrado com esta matrícula.", ex);
            }
            if (msg.contains("protocolo")) {
                throw new BusinessException("Já existe um chamado com este número de protocolo.", ex);
            }
            throw new BusinessException("Violação de integridade no banco de dados: " + ex.getMostSpecificCause().getMessage(), ex);
        }
    }
}

