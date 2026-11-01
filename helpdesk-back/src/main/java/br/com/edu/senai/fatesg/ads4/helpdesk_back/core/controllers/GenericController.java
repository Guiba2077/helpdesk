package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.dtos.BaseDTO;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller genérico (Template Method) com endpoints REST mapeados para listagem,
 * busca por ID, ativação/inativação e exclusão lógica (soft delete).
 */
public abstract class GenericController<D extends BaseDTO> {

    protected abstract IGenericService<D> getService();

    @GetMapping
    public ResponseEntity<List<D>> listar(
            @RequestParam(name = "incluirInativos", defaultValue = "false") boolean incluirInativos) {
        return ResponseEntity.ok(getService().listarTodos(incluirInativos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(getService().buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id) {
        getService().toggleStatus(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        getService().deletar(id);
        return ResponseEntity.noContent().build();
    }
}

