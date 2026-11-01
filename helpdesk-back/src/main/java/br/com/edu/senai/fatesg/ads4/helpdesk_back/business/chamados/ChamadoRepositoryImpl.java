package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos.TecnicoJpaRepository;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos.TecnicoModel;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioJpaRepository;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioModel;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository("businessChamadoRepository")
public class ChamadoRepositoryImpl implements IChamadoRepository {

    private final ChamadoJpaRepository jpa;
    private final UsuarioJpaRepository usuarioJpa;
    private final TecnicoJpaRepository tecnicoJpa;
    private final ApontamentoJpaRepository apontamentoJpa;
    private final HistoricoChamadoJpaRepository historicoJpa;
    private final JdbcTemplate jdbc;

    public ChamadoRepositoryImpl(
            ChamadoJpaRepository jpa,
            UsuarioJpaRepository usuarioJpa,
            TecnicoJpaRepository tecnicoJpa,
            ApontamentoJpaRepository apontamentoJpa,
            HistoricoChamadoJpaRepository historicoJpa,
            JdbcTemplate jdbc) {
        this.jpa = jpa;
        this.usuarioJpa = usuarioJpa;
        this.tecnicoJpa = tecnicoJpa;
        this.apontamentoJpa = apontamentoJpa;
        this.historicoJpa = historicoJpa;
        this.jdbc = jdbc;
    }

    private void enriquecerComRelacionamentos(ChamadoModel chamado) {
        if (chamado == null) return;
        if (chamado.getSolicitanteId() != null) {
            usuarioJpa.findById(chamado.getSolicitanteId()).ifPresent(u -> {
                chamado.setSolicitanteNome(u.getNome());
                chamado.setSolicitanteEmail(u.getEmail());
                chamado.setSolicitanteSetor(u.getSetor());
            });
        }
        if (chamado.getTecnicoId() != null) {
            tecnicoJpa.findById(chamado.getTecnicoId()).ifPresent(t -> {
                chamado.setTecnicoEspecialidade(t.getEspecialidade());
                if (t.getUsuarioId() != null) {
                    usuarioJpa.findById(t.getUsuarioId()).ifPresent(u -> chamado.setTecnicoNome(u.getNome()));
                }
            });
        }
    }

    private void enriquecerLista(List<ChamadoModel> lista) {
        if (lista == null || lista.isEmpty()) return;

        List<Long> solicitanteIds = lista.stream()
                .map(ChamadoModel::getSolicitanteId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, UsuarioModel> mapaUsuarios = usuarioJpa.findAllById(solicitanteIds).stream()
                .collect(Collectors.toMap(UsuarioModel::getId, u -> u));

        List<Long> tecnicoIds = lista.stream()
                .map(ChamadoModel::getTecnicoId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, TecnicoModel> mapaTecnicos = tecnicoJpa.findAllById(tecnicoIds).stream()
                .collect(Collectors.toMap(TecnicoModel::getId, t -> t));

        List<Long> tecUserIds = mapaTecnicos.values().stream()
                .map(TecnicoModel::getUsuarioId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> mapaTecNomes = usuarioJpa.findAllById(tecUserIds).stream()
                .collect(Collectors.toMap(UsuarioModel::getId, UsuarioModel::getNome));

        for (ChamadoModel c : lista) {
            UsuarioModel solicitante = mapaUsuarios.get(c.getSolicitanteId());
            if (solicitante != null) {
                c.setSolicitanteNome(solicitante.getNome());
                c.setSolicitanteEmail(solicitante.getEmail());
                c.setSolicitanteSetor(solicitante.getSetor());
            }

            if (c.getTecnicoId() != null) {
                TecnicoModel tec = mapaTecnicos.get(c.getTecnicoId());
                if (tec != null) {
                    c.setTecnicoEspecialidade(tec.getEspecialidade());
                    c.setTecnicoNome(mapaTecNomes.get(tec.getUsuarioId()));
                }
            }
        }
    }

    @Override
    public Optional<ChamadoModel> buscarPorId(Long id) {
        Optional<ChamadoModel> opt = jpa.findById(id);
        opt.ifPresent(this::enriquecerComRelacionamentos);
        return opt;
    }

    @Override
    public List<ChamadoModel> listarTodos() {
        List<ChamadoModel> lista = jpa.findByAtivoTrueOrderByDataAberturaDesc();
        enriquecerLista(lista);
        return lista;
    }

    @Override
    public List<ChamadoModel> listarTodosIncluindoInativos() {
        List<ChamadoModel> lista = jpa.findAllByOrderByDataAberturaDesc();
        enriquecerLista(lista);
        return lista;
    }

    @Override
    public List<ChamadoModel> buscarPorStatus(String status) {
        List<ChamadoModel> lista = jpa.findByStatusIgnoreCaseAndAtivoTrueOrderByDataAberturaDesc(status);
        enriquecerLista(lista);
        return lista;
    }

    @Override
    @Transactional
    public ChamadoModel salvar(ChamadoModel entity) {
        ChamadoModel salvo = jpa.save(entity);
        return buscarPorId(salvo.getId()).orElse(salvo);
    }

    @Override
    @Transactional
    public void atualizar(ChamadoModel entity) {
        jpa.save(entity);
    }

    @Override
    @Transactional
    public void alocarTecnicoViaProcedure(Long chamadoId, Long tecnicoId, Long usuarioResponsavelId) {
        jpa.chamarSpAlocarChamado(chamadoId, tecnicoId, usuarioResponsavelId);
    }

    @Override
    @Transactional
    public void atualizarPrioridade(Long chamadoId, String novaPrioridade, String justificativa) {
        if (justificativa != null && !justificativa.isBlank()) {
            jdbc.queryForObject("SELECT set_config('app.motivo_prioridade', ?, true)", String.class, justificativa.trim());
        }
        ChamadoModel chamado = jpa.findById(chamadoId).orElse(null);
        if (chamado != null) {
            chamado.setPrioridade(novaPrioridade.toUpperCase());
            chamado.setAtualizadoEm(java.time.LocalDateTime.now());
            jpa.save(chamado);
        }
    }

    @Override
    @Transactional
    public void encerrarChamado(Long chamadoId, String solucao) {
        jpa.encerrarChamado(chamadoId, solucao);
    }

    @Override
    public List<HistoricoChamado> listarHistoricoPorChamado(Long chamadoId) {
        List<HistoricoChamado> lista = historicoJpa.findByChamadoIdOrderByDataMudancaAscIdAsc(chamadoId);
        if (lista != null && !lista.isEmpty()) {
            List<Long> userIds = lista.stream()
                    .map(HistoricoChamado::getUsuarioResponsavelId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            Map<Long, String> nomes = usuarioJpa.findAllById(userIds).stream()
                    .collect(Collectors.toMap(UsuarioModel::getId, UsuarioModel::getNome));
            for (HistoricoChamado h : lista) {
                if (h.getUsuarioResponsavelId() != null) {
                    h.setUsuarioResponsavelNome(nomes.getOrDefault(h.getUsuarioResponsavelId(), "Usuário #" + h.getUsuarioResponsavelId()));
                } else {
                    h.setUsuarioResponsavelNome("Sistema / Trigger");
                }
            }
        }
        return lista;
    }

    @Override
    @Transactional
    public ApontamentoAtendimento adicionarApontamento(Long chamadoId, Long autorId, String descricao) {
        ApontamentoAtendimento apontamento = ApontamentoAtendimento.builder()
                .chamadoId(chamadoId)
                .autorId(autorId)
                .descricao(descricao)
                .build();
        ApontamentoAtendimento salvo = apontamentoJpa.save(apontamento);
        usuarioJpa.findById(autorId).ifPresent(u -> salvo.setAutorNome(u.getNome()));
        return salvo;
    }

    @Override
    public List<ApontamentoAtendimento> listarApontamentosPorChamado(Long chamadoId) {
        List<ApontamentoAtendimento> lista = apontamentoJpa.findByChamadoIdOrderByCriadoEmAsc(chamadoId);
        if (lista != null && !lista.isEmpty()) {
            List<Long> autores = lista.stream()
                    .map(ApontamentoAtendimento::getAutorId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            Map<Long, String> nomes = usuarioJpa.findAllById(autores).stream()
                    .collect(Collectors.toMap(UsuarioModel::getId, UsuarioModel::getNome));
            for (ApontamentoAtendimento a : lista) {
                a.setAutorNome(nomes.getOrDefault(a.getAutorId(), "Técnico TI"));
            }
        }
        return lista;
    }

    @Override
    public DashboardChamadoDTO obterResumoDashboard() {
        List<Object[]> rows = jpa.obterResumoDashboardRaw();
        if (rows != null && !rows.isEmpty()) {
            Object[] r = rows.get(0);
            return new DashboardChamadoDTO(
                    ((Number) r[0]).longValue(),
                    ((Number) r[1]).longValue(),
                    ((Number) r[2]).longValue(),
                    ((Number) r[3]).longValue(),
                    ((Number) r[4]).longValue());
        }
        return new DashboardChamadoDTO(0L, 0L, 0L, 0L, 0L);
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
