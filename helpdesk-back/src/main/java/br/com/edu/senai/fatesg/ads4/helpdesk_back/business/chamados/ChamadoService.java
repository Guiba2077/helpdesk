package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.EntidadeNaoEncontradaException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.NegocioException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.patterns.SessionContextManager;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.GenericService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("businessChamadoService")
public class ChamadoService extends GenericService<ChamadoModel, ChamadoDTO, IChamadoRepository>
        implements IChamadoService {

    private final ChamadoMapper mapper;
    private final IChamadoValidation validation;
    private final SessionContextManager sessionContextManager;

    public ChamadoService(
            IChamadoRepository repository,
            ChamadoMapper mapper,
            IChamadoValidation validation,
            SessionContextManager sessionContextManager) {
        super(repository);
        this.mapper = mapper;
        this.validation = validation;
        this.sessionContextManager = sessionContextManager;
    }

    @Override
    protected ChamadoDTO toDTO(ChamadoModel model) {
        ChamadoDTO dto = mapper.toDto(model);
        if (dto != null && dto.getId() != null) {
            dto.setHistoricos(listarHistorico(dto.getId()));
            dto.setApontamentos(listarApontamentos(dto.getId()));
        }
        return dto;
    }

    @Override
    @Transactional
    public ChamadoDTO abrirChamado(ChamadoModel model, Long usuarioLogadoId) {
        if (model.getSolicitanteId() == null && usuarioLogadoId != null) {
            model.setSolicitanteId(usuarioLogadoId);
        }
        validation.validar(model);
        PrioridadeChamado prioridade = PrioridadeChamado.fromString(model.getPrioridade());
        CategoriaChamado categoria = CategoriaChamado.fromString(model.getCategoria());
        model.setPrioridade(prioridade.name());
        model.setCategoria(categoria.name());
        model.setStatus(StatusChamado.ABERTO.name());

        sessionContextManager.definirUsuarioSessao(
                usuarioLogadoId != null ? usuarioLogadoId : model.getSolicitanteId());

        return executarComTratamentoDuplicidade(() -> toDTO(repository.salvar(model)));
    }

    @Override
    @Transactional
    public ChamadoDTO atualizarDadosBasicos(Long id, ChamadoModel model, Long usuarioLogadoId) {
        ChamadoModel existente = repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + id));
        model.setId(existente.getId());
        if (model.getSolicitanteId() == null) {
            model.setSolicitanteId(existente.getSolicitanteId());
        }
        validation.validar(model);
        sessionContextManager.definirUsuarioSessao(usuarioLogadoId);
        repository.atualizar(model);
        return buscarPorId(id);
    }

    @Override
    @Transactional
    public ChamadoDTO alocarTecnico(Long chamadoId, Long tecnicoId, Long usuarioLogadoId) {
        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));

        ChamadoTemplate.validarEIniciarAtendimento(chamado, tecnicoId);
        Long responsavelEfetivo = (usuarioLogadoId != null && usuarioLogadoId > 0) ? usuarioLogadoId : 1L;
        sessionContextManager.definirUsuarioSessao(responsavelEfetivo);

        // Aciona a Procedure app.sp_alocar_chamado no PostgreSQL (que também dispara a Trigger de histórico)
        repository.alocarTecnicoViaProcedure(chamadoId, tecnicoId, responsavelEfetivo);

        return buscarPorId(chamadoId);
    }

    @Override
    @Transactional
    public ChamadoDTO alterarPrioridade(
            Long chamadoId,
            String novaPrioridade,
            String justificativa,
            Long usuarioLogadoId) {
        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));

        if (StatusChamado.fromString(chamado.getStatus()) == StatusChamado.FECHADO) {
            throw new NegocioException("Não é permitido alterar a prioridade de um chamado já FECHADO.");
        }

        PrioridadeChamado destino = PrioridadeChamado.fromString(novaPrioridade);
        sessionContextManager.definirUsuarioSessao(usuarioLogadoId);

        // Dispara automaticamente a Trigger trg_auditoria_prioridade no schema audit
        repository.atualizarPrioridade(chamadoId, destino.name(), justificativa);

        return buscarPorId(chamadoId);
    }

    @Override
    @Transactional
    public ChamadoDTO encerrarChamado(Long chamadoId, String solucao, Long usuarioLogadoId) {
        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));

        ChamadoTemplate.validarEEncerrar(chamado, solucao);
        sessionContextManager.definirUsuarioSessao(usuarioLogadoId);

        // Dispara automaticamente a Trigger trg_historico_status (ATENDIMENTO -> FECHADO)
        repository.encerrarChamado(chamadoId, chamado.getSolucao());

        return buscarPorId(chamadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChamadoDTO> buscarPorStatus(String status) {
        StatusChamado filtro = StatusChamado.fromString(status);
        List<ChamadoModel> todos = repository.listarTodos();
        ChamadoIterator iterator = new ChamadoIterator(todos, filtro);
        List<ChamadoDTO> filtrados = new ArrayList<>();
        while (iterator.hasNext()) {
            filtrados.add(toDTO(iterator.next()));
        }
        return filtrados;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricoChamadoDTO> listarHistorico(Long chamadoId) {
        return repository.listarHistoricoPorChamado(chamadoId)
                .stream()
                .map(mapper::toHistoricoDto)
                .toList();
    }

    @Override
    @Transactional
    public ApontamentoAtendimentoDTO adicionarApontamento(Long chamadoId, Long autorId, String descricao) {
        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));
        if (StatusChamado.fromString(chamado.getStatus()) == StatusChamado.FECHADO) {
            throw new NegocioException("Não é possível adicionar apontamentos em um chamado já FECHADO.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new NegocioException("A mensagem/descrição do apontamento de atendimento é obrigatória.");
        }
        Long autorEfetivo = (autorId != null && autorId > 0) ? autorId : 1L;
        sessionContextManager.definirUsuarioSessao(autorEfetivo);
        ApontamentoAtendimento salvo = repository.adicionarApontamento(chamadoId, autorEfetivo, descricao.trim());
        return mapper.toApontamentoDto(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApontamentoAtendimentoDTO> listarApontamentos(Long chamadoId) {
        return repository.listarApontamentosPorChamado(chamadoId)
                .stream()
                .map(mapper::toApontamentoDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardChamadoDTO obterResumoDashboard() {
        return repository.obterResumoDashboard();
    }
}

