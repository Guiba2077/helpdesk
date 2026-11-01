package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.IUsuarioService;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios.UsuarioDTO;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers.GenericController;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.NegocioException;
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
@RequestMapping("/api/tecnicos")
public class TecnicoController extends GenericController<TecnicoDTO> {

    private final ITecnicoService service;
    private final IUsuarioService usuarioService;

    public TecnicoController(ITecnicoService service, IUsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @Override
    protected IGenericService<TecnicoDTO> getService() {
        return service;
    }

    private void exigirAdmin(Long usuarioLogadoId) {
        UsuarioDTO logado = usuarioService.buscarPorId(usuarioLogadoId != null ? usuarioLogadoId : 1L);
        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            throw new NegocioException(
                    "Acesso negado: Apenas usuários com perfil ADMIN podem cadastrar ou gerenciar técnicos de TI.");
        }
    }

    public record SalvarTecnicoRequest(
            @NotNull(message = "O ID do usuário vinculado é obrigatório") Long usuarioId,
            @NotBlank(message = "A matrícula é obrigatória") String matricula,
            @NotBlank(message = "A especialidade é obrigatória") String especialidade,
            @NotBlank(message = "O nível de atendimento é obrigatório (N1, N2, N3)") String nivelAtendimento,
            Boolean disponivel) {
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<TecnicoDTO>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    public record CriarEspecialidadeRequest(
            @NotBlank(message = "O código é obrigatório (ex: CLOUD_AWS)") String codigo,
            @NotBlank(message = "O nome é obrigatório") String nome,
            String nivelPadrao,
            String descricao) {
    }

    @GetMapping("/especialidades")
    public ResponseEntity<List<EspecialidadeDTO>> listarEspecialidades() {
        return ResponseEntity.ok(service.listarEspecialidades());
    }

    @PostMapping("/especialidades")
    public ResponseEntity<EspecialidadeDTO> cadastrarEspecialidade(
            @Valid @RequestBody CriarEspecialidadeRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        exigirAdmin(usuarioLogadoId);
        EspecialidadeDTO salva = service.cadastrarEspecialidade(
                request.codigo(), request.nome(), request.nivelPadrao(), request.descricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PostMapping
    public ResponseEntity<TecnicoDTO> criar(
            @Valid @RequestBody SalvarTecnicoRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        exigirAdmin(usuarioLogadoId);
        TecnicoModel model = TecnicoModel.builder()
                .usuarioId(request.usuarioId())
                .matricula(request.matricula())
                .especialidade(request.especialidade())
                .nivelAtendimento(request.nivelAtendimento())
                .disponivel(request.disponivel() == null || request.disponivel())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TecnicoDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalvarTecnicoRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        exigirAdmin(usuarioLogadoId);
        TecnicoModel model = TecnicoModel.builder()
                .usuarioId(request.usuarioId())
                .matricula(request.matricula())
                .especialidade(request.especialidade())
                .nivelAtendimento(request.nivelAtendimento())
                .disponivel(request.disponivel() == null || request.disponivel())
                .build();
        return ResponseEntity.ok(service.atualizar(id, model));
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<TecnicoDTO> alternarDisponibilidade(
            @PathVariable Long id,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        exigirAdmin(usuarioLogadoId);
        return ResponseEntity.ok(service.alternarDisponibilidade(id));
    }
}
