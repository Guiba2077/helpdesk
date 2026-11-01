package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.auditoria;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioJpaRepository;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioModel;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository("businessAuditoriaPrioridadeRepository")
public class AuditoriaPrioridadeRepositoryImpl implements IAuditoriaPrioridadeRepository {

    private final AuditoriaPrioridadeJpaRepository jpa;
    private final UsuarioJpaRepository usuarioJpa;

    public AuditoriaPrioridadeRepositoryImpl(AuditoriaPrioridadeJpaRepository jpa, UsuarioJpaRepository usuarioJpa) {
        this.jpa = jpa;
        this.usuarioJpa = usuarioJpa;
    }

    private void enriquecerLista(List<AuditoriaPrioridadeModel> lista) {
        if (lista == null || lista.isEmpty()) return;
        List<Long> idsUsuarios = lista.stream()
                .map(AuditoriaPrioridadeModel::getAlteradoPorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> mapaNomes = usuarioJpa.findAllById(idsUsuarios).stream()
                .collect(Collectors.toMap(UsuarioModel::getId, UsuarioModel::getNome));

        for (AuditoriaPrioridadeModel item : lista) {
            if (item.getAlteradoPorId() != null) {
                item.setAlteradoPorNome(mapaNomes.getOrDefault(item.getAlteradoPorId(), "Usuário #" + item.getAlteradoPorId()));
            } else {
                item.setAlteradoPorNome("Sistema / Admin");
            }
        }
    }

    @Override
    public Optional<AuditoriaPrioridadeModel> buscarPorId(Long id) {
        Optional<AuditoriaPrioridadeModel> opt = jpa.findById(id);
        opt.ifPresent(item -> {
            if (item.getAlteradoPorId() != null) {
                usuarioJpa.findById(item.getAlteradoPorId())
                        .ifPresent(u -> item.setAlteradoPorNome(u.getNome()));
            }
        });
        return opt;
    }

    @Override
    public List<AuditoriaPrioridadeModel> listarTodos() {
        List<AuditoriaPrioridadeModel> lista = jpa.findAllOrderByDataAlteracaoDesc();
        enriquecerLista(lista);
        return lista;
    }

    @Override
    public List<AuditoriaPrioridadeModel> listarTodosIncluindoInativos() {
        return listarTodos();
    }

    @Override
    public List<AuditoriaPrioridadeModel> listarPorChamado(Long chamadoId) {
        List<AuditoriaPrioridadeModel> lista = jpa.findByChamadoIdOrderByDataAlteracaoDesc(chamadoId);
        enriquecerLista(lista);
        return lista;
    }

    @Override
    public AuditoriaPrioridadeModel salvar(AuditoriaPrioridadeModel entity) {
        return jpa.save(entity);
    }

    @Override
    public void atualizar(AuditoriaPrioridadeModel entity) {
        // Auditoria é imutável no banco de dados (schema audit)
    }

    @Override
    public void deletar(Long id) {
        // Auditoria é imutável no banco de dados (schema audit)
    }

    @Override
    public void toggleStatus(Long id) {
        // Auditoria é imutável
    }
}
