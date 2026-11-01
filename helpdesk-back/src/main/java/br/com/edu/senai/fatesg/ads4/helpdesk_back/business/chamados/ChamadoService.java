package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.IUsuarioRepository;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.PerfilUsuario;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.Usuario;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioFactory;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioModel;
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
    private final IUsuarioRepository usuarioRepository;
    private final SessionContextManager sessionContextManager;

    public ChamadoService(
            IChamadoRepository repository,
            ChamadoMapper mapper,
            IChamadoValidation validation,
            IUsuarioRepository usuarioRepository,
            SessionContextManager sessionContextManager) {
        super(repository);
        this.mapper = mapper;
        this.validation = validation;
        this.usuarioRepository = usuarioRepository;
        this.sessionContextManager = sessionContextManager;
    }

    private Usuario obterUsuarioAtivo(Long usuarioId) {
        Long idEfetivo = (usuarioId != null && usuarioId > 0) ? usuarioId : 1L;
        UsuarioModel model = usuarioRepository.buscarPorId(idEfetivo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Usuário da sessão não encontrado com ID: " + idEfetivo));
        if (!model.isAtivo()) {
            throw new NegocioException("O usuário informado na sessão está inativo.");
        }
        return UsuarioFactory.fromModel(model);
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
        Long idSolicitante = usuarioLogadoId != null ? usuarioLogadoId : model.getSolicitanteId();
        Usuario usuario = obterUsuarioAtivo(idSolicitante);

        // Regra de Permissão: Apenas OPERADOR e ADMIN podem abrir chamados (TECNICO apenas atende)
        if (usuario.isTecnico()) {
            throw new NegocioException(
                    "Acesso negado: Usuários com perfil TECNICO atuam exclusivamente no atendimento e não podem abrir chamados. Utilize um usuário OPERADOR ou ADMIN.");
        }

        model.setSolicitanteId(usuario.getId());
        validation.validar(model);
        PrioridadeChamado prioridade = PrioridadeChamado.fromString(model.getPrioridade());
        CategoriaChamado categoria = CategoriaChamado.fromString(model.getCategoria());
        model.setPrioridade(prioridade.name());
        model.setCategoria(categoria.name());
        model.setStatus(StatusChamado.ABERTO.name());

        sessionContextManager.definirContextoUsuario(usuario.getId(), usuario.getPerfil().name());

        return executarComTratamentoDuplicidade(() -> toDTO(repository.salvar(model)));
    }

    @Override
    @Transactional
    public ChamadoDTO atualizarDadosBasicos(Long id, ChamadoModel model, Long usuarioLogadoId) {
        Usuario usuario = obterUsuarioAtivo(usuarioLogadoId);
        ChamadoModel existente = repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + id));
        model.setId(existente.getId());
        if (model.getSolicitanteId() == null) {
            model.setSolicitanteId(existente.getSolicitanteId());
        }
        validation.validar(model);
        sessionContextManager.definirContextoUsuario(usuario.getId(), usuario.getPerfil().name());
        repository.atualizar(model);
        return buscarPorId(id);
    }

    @Override
    @Transactional
    public ChamadoDTO alocarTecnico(Long chamadoId, Long tecnicoId, Long usuarioLogadoId) {
        Usuario usuario = obterUsuarioAtivo(usuarioLogadoId);

        // Regra de Permissão: Somente ADMIN pode alocar técnico no chamado
        if (!usuario.isAdmin()) {
            throw new NegocioException(
                    "Acesso negado: Apenas usuários com perfil ADMIN podem alocar técnicos nos chamados (perfil atual: "
                            + usuario.getPerfil().name() + ").");
        }

        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));

        ChamadoTemplate.validarEIniciarAtendimento(chamado, tecnicoId);
        sessionContextManager.definirContextoUsuario(usuario.getId(), usuario.getPerfil().name());

        // Aciona a Procedure app.sp_alocar_chamado no PostgreSQL (que também dispara a Trigger de histórico)
        repository.alocarTecnicoViaProcedure(chamadoId, tecnicoId, usuario.getId());

        return buscarPorId(chamadoId);
    }

    @Override
    @Transactional
    public ChamadoDTO alterarPrioridade(
            Long chamadoId,
            String novaPrioridade,
            String justificativa,
            Long usuarioLogadoId) {
        Usuario usuario = obterUsuarioAtivo(usuarioLogadoId);

        // Regra de Permissão: Somente ADMIN pode alterar a prioridade do chamado
        if (!usuario.isAdmin()) {
            throw new NegocioException(
                    "Acesso negado: Apenas usuários com perfil ADMIN podem alterar a prioridade de um chamado (perfil atual: "
                            + usuario.getPerfil().name() + ").");
        }

        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));

        if (StatusChamado.fromString(chamado.getStatus()) == StatusChamado.FECHADO) {
            throw new NegocioException("Não é permitido alterar a prioridade de um chamado já FECHADO.");
        }

        PrioridadeChamado destino = PrioridadeChamado.fromString(novaPrioridade);
        sessionContextManager.definirContextoUsuario(usuario.getId(), usuario.getPerfil().name());

        // Dispara automaticamente a Trigger trg_auditoria_prioridade no schema audit
        repository.atualizarPrioridade(chamadoId, destino.name(), justificativa);

        return buscarPorId(chamadoId);
    }

    @Override
    @Transactional
    public ChamadoDTO encerrarChamado(Long chamadoId, String solucao, Long usuarioLogadoId) {
        Usuario usuario = obterUsuarioAtivo(usuarioLogadoId);

        // Regra de Permissão: Apenas TECNICO e ADMIN podem encerrar um chamado
        if (usuario.getPerfil() == PerfilUsuario.OPERADOR) {
            throw new NegocioException(
                    "Acesso negado: Usuários com perfil OPERADOR não têm permissão para encerrar chamados. Apenas TECNICO ou ADMIN podem concluir o atendimento.");
        }

        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));

        ChamadoTemplate.validarEEncerrar(chamado, solucao);
        sessionContextManager.definirContextoUsuario(usuario.getId(), usuario.getPerfil().name());

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
        Usuario usuario = obterUsuarioAtivo(autorId);

        // Regra de Permissão: Apenas TECNICO e ADMIN podem registrar apontamento técnico
        if (usuario.getPerfil() == PerfilUsuario.OPERADOR) {
            throw new NegocioException(
                    "Acesso negado: Usuários com perfil OPERADOR não têm permissão para registrar apontamentos técnicos.");
        }

        ChamadoModel chamado = repository.buscarPorId(chamadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chamado não encontrado com ID: " + chamadoId));
        if (StatusChamado.fromString(chamado.getStatus()) == StatusChamado.FECHADO) {
            throw new NegocioException("Não é possível adicionar apontamentos em um chamado já FECHADO.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new NegocioException("A mensagem/descrição do apontamento de atendimento é obrigatória.");
        }

        sessionContextManager.definirContextoUsuario(usuario.getId(), usuario.getPerfil().name());
        ApontamentoAtendimento salvo = repository.adicionarApontamento(chamadoId, usuario.getId(), descricao.trim());
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
