package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers.GenericController;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController extends GenericController<ChamadoDTO> {

    private final IChamadoService service;

    public ChamadoController(IChamadoService service) {
        this.service = service;
    }

    @Override
    protected IGenericService<ChamadoDTO> getService() {
        return service;
    }

    public record AbrirChamadoRequest(
            @NotBlank(message = "O título do chamado é obrigatório") String titulo,
            @NotBlank(message = "A descrição do problema é obrigatória") String descricao,
            String categoria,
            String prioridade,
            Long solicitanteId) {
    }

    public record AlocarTecnicoRequest(
            @NotNull(message = "O ID do técnico é obrigatório") Long tecnicoId) {
    }

    public record AlterarPrioridadeRequest(
            @NotBlank(message = "A nova prioridade é obrigatória (BAIXA, MEDIA, ALTA, CRITICA)") String prioridade,
            String justificativa) {
    }

    public record EncerrarChamadoRequest(
            @NotBlank(message = "A descrição da solução é obrigatória para encerrar o chamado") String solucao) {
    }

    public record CriarApontamentoRequest(
            @NotBlank(message = "A descrição do apontamento é obrigatória") String descricao,
            Long autorId) {
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ChamadoDTO>> listar(
            @org.springframework.web.bind.annotation.RequestParam(name = "incluirInativos", defaultValue = "false") boolean incluirInativos) {
        return ResponseEntity.ok(service.listarTodos(incluirInativos));
    }

    @GetMapping(headers = "X-Usuario-Id")
    public ResponseEntity<List<ChamadoDTO>> listarPorUsuario(
            @org.springframework.web.bind.annotation.RequestParam(name = "incluirInativos", defaultValue = "false") boolean incluirInativos,
            @RequestHeader(name = "X-Usuario-Id") Long usuarioLogadoId) {
        return ResponseEntity.ok(service.listarChamadosPorUsuario(usuarioLogadoId, incluirInativos));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChamadoDTO>> buscarPorStatus(
            @PathVariable String status,
            @RequestHeader(name = "X-Usuario-Id", required = false) Long usuarioLogadoId) {
        return ResponseEntity.ok(usuarioLogadoId != null
                ? service.buscarPorStatus(status, usuarioLogadoId)
                : service.buscarPorStatus(status));
    }

    @GetMapping("/dashboard/resumo")
    public ResponseEntity<DashboardChamadoDTO> obterResumoDashboard() {
        return ResponseEntity.ok(service.obterResumoDashboard());
    }

    @PostMapping
    public ResponseEntity<ChamadoDTO> abrirChamado(
            @Valid @RequestBody AbrirChamadoRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        ChamadoModel model = ChamadoModel.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .categoria(request.categoria() != null ? request.categoria() : "SOFTWARE")
                .prioridade(request.prioridade() != null ? request.prioridade() : "MEDIA")
                .solicitanteId(request.solicitanteId() != null ? request.solicitanteId() : usuarioLogadoId)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.abrirChamado(model, usuarioLogadoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChamadoDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AbrirChamadoRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        ChamadoModel model = ChamadoModel.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .categoria(request.categoria())
                .solicitanteId(request.solicitanteId())
                .build();
        return ResponseEntity.ok(service.atualizarDadosBasicos(id, model, usuarioLogadoId));
    }

    @PostMapping("/{id}/alocar")
    public ResponseEntity<ChamadoDTO> alocarTecnico(
            @PathVariable Long id,
            @Valid @RequestBody AlocarTecnicoRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        return ResponseEntity.ok(service.alocarTecnico(id, request.tecnicoId(), usuarioLogadoId));
    }

    @PatchMapping("/{id}/prioridade")
    public ResponseEntity<ChamadoDTO> alterarPrioridade(
            @PathVariable Long id,
            @Valid @RequestBody AlterarPrioridadeRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        return ResponseEntity.ok(service.alterarPrioridade(
                id,
                request.prioridade(),
                request.justificativa(),
                usuarioLogadoId));
    }

    @PostMapping("/{id}/encerrar")
    public ResponseEntity<ChamadoDTO> encerrarChamado(
            @PathVariable Long id,
            @Valid @RequestBody EncerrarChamadoRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        return ResponseEntity.ok(service.encerrarChamado(id, request.solucao(), usuarioLogadoId));
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoChamadoDTO>> listarHistorico(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarHistorico(id));
    }

    @GetMapping("/{id}/apontamentos")
    public ResponseEntity<List<ApontamentoAtendimentoDTO>> listarApontamentos(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarApontamentos(id));
    }

    @PostMapping("/{id}/apontamentos")
    public ResponseEntity<ApontamentoAtendimentoDTO> adicionarApontamento(
            @PathVariable Long id,
            @Valid @RequestBody CriarApontamentoRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        Long autorEfetivo = request.autorId() != null ? request.autorId() : usuarioLogadoId;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.adicionarApontamento(id, autorEfetivo, request.descricao()));
    }
}

