package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioJpaRepository;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioModel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository("businessTecnicoRepository")
public class TecnicoRepositoryImpl implements ITecnicoRepository {

    private final TecnicoJpaRepository jpa;
    private final UsuarioJpaRepository usuarioJpa;
    private final EspecialidadeJpaRepository especialidadeJpa;

    public TecnicoRepositoryImpl(
            TecnicoJpaRepository jpa,
            UsuarioJpaRepository usuarioJpa,
            EspecialidadeJpaRepository especialidadeJpa) {
        this.jpa = jpa;
        this.usuarioJpa = usuarioJpa;
        this.especialidadeJpa = especialidadeJpa;
    }

    private void enriquecerComUsuario(TecnicoModel tecnico) {
        if (tecnico != null && tecnico.getUsuarioId() != null) {
            usuarioJpa.findById(tecnico.getUsuarioId()).ifPresent(u -> {
                tecnico.setNome(u.getNome());
                tecnico.setEmail(u.getEmail());
                tecnico.setTelefone(u.getTelefone());
                tecnico.setSetor(u.getSetor());
            });
        }
    }

    private void enriquecerLista(List<TecnicoModel> lista) {
        if (lista == null || lista.isEmpty()) return;
        List<Long> idsUsuarios = lista.stream().map(TecnicoModel::getUsuarioId).toList();
        Map<Long, UsuarioModel> mapaUsuarios = usuarioJpa.findAllById(idsUsuarios).stream()
                .collect(Collectors.toMap(UsuarioModel::getId, u -> u));
        for (TecnicoModel t : lista) {
            UsuarioModel u = mapaUsuarios.get(t.getUsuarioId());
            if (u != null) {
                t.setNome(u.getNome());
                t.setEmail(u.getEmail());
                t.setTelefone(u.getTelefone());
                t.setSetor(u.getSetor());
            }
        }
    }

    @Override
    public Optional<TecnicoModel> buscarPorId(Long id) {
        Optional<TecnicoModel> opt = jpa.findById(id);
        opt.ifPresent(this::enriquecerComUsuario);
        return opt;
    }

    @Override
    public List<TecnicoModel> listarTodos() {
        List<TecnicoModel> lista = jpa.findByAtivoTrueOrderById();
        enriquecerLista(lista);
        return lista;
    }

    @Override
    public List<TecnicoModel> listarTodosIncluindoInativos() {
        List<TecnicoModel> lista = jpa.findAllByOrderById();
        enriquecerLista(lista);
        return lista;
    }

    @Override
    public List<TecnicoModel> listarDisponiveis() {
        List<TecnicoModel> lista = jpa.findDisponiveis();
        enriquecerLista(lista);
        return lista;
    }

    @Override
    public List<Especialidade> listarEspecialidades() {
        return especialidadeJpa.findAllByAtivoTrueOrderByNomeAsc().stream()
                .map(e -> new Especialidade(e.getCodigo(), e.getNome(), e.getNivelPadrao()))
                .toList();
    }

    @Override
    @Transactional
    public Especialidade cadastrarEspecialidade(Especialidade esp) {
        EspecialidadeModel model = EspecialidadeModel.builder()
                .codigo(esp.getCodigo().toUpperCase().trim())
                .nome(esp.getNome().trim())
                .nivelPadrao(esp.getNivelAtendimento() != null ? esp.getNivelAtendimento().toUpperCase().trim() : "N1")
                .ativo(true)
                .build();
        EspecialidadeModel salvo = especialidadeJpa.save(model);
        return new Especialidade(salvo.getCodigo(), salvo.getNome(), salvo.getNivelPadrao());
    }

    @Override
    @Transactional
    public TecnicoModel salvar(TecnicoModel entity) {
        TecnicoModel salvo = jpa.save(entity);
        enriquecerComUsuario(salvo);
        return salvo;
    }

    @Override
    @Transactional
    public void atualizar(TecnicoModel entity) {
        TecnicoModel existente = jpa.findById(entity.getId()).orElse(entity);
        existente.setMatricula(entity.getMatricula());
        existente.setEspecialidade(entity.getEspecialidade());
        existente.setNivelAtendimento(entity.getNivelAtendimento());
        existente.setDisponivel(entity.isDisponivel());
        jpa.save(existente);
    }

    @Override
    @Transactional
    public void alternarDisponibilidade(Long id) {
        jpa.alternarDisponibilidade(id);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        jpa.inativar(id);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        jpa.alternarStatus(id);
    }
}
