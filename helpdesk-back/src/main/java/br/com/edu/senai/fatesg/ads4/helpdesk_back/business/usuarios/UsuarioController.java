package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.controllers.GenericController;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.NegocioException;
import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController extends GenericController<UsuarioDTO> {

    private final IUsuarioService service;

    public UsuarioController(IUsuarioService service) {
        this.service = service;
    }

    @Override
    protected IGenericService<UsuarioDTO> getService() {
        return service;
    }

    private void exigirAdmin(Long usuarioLogadoId) {
        UsuarioDTO logado = service.buscarPorId(usuarioLogadoId != null ? usuarioLogadoId : 1L);
        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            throw new NegocioException(
                    "Acesso negado: Apenas usuários com perfil ADMIN podem cadastrar ou gerenciar usuários.");
        }
    }

    public record LoginRequest(
            @NotBlank(message = "O e-mail é obrigatório") @Email String email,
            @NotBlank(message = "A senha é obrigatória") String senha) {
    }

    public record CriarUsuarioRequest(
            @NotBlank(message = "O nome é obrigatório") String nome,
            @NotBlank(message = "O e-mail é obrigatório") @Email String email,
            String senha,
            String telefone,
            String setor,
            @NotBlank(message = "O perfil é obrigatório (ADMIN, TECNICO ou OPERADOR)") String perfil) {
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@Valid @RequestBody LoginRequest request) {
        UsuarioDTO usuario = service.autenticar(request.email(), request.senha());
        return ResponseEntity.ok()
                .header("X-Usuario-Id", String.valueOf(usuario.getId()))
                .header("X-Usuario-Perfil", usuario.getPerfil())
                .body(usuario);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNome(@RequestParam(defaultValue = "") String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(
            @Valid @RequestBody CriarUsuarioRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        exigirAdmin(usuarioLogadoId);
        UsuarioModel model = UsuarioModel.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha() != null ? request.senha() : "123456")
                .telefone(request.telefone())
                .setor(request.setor())
                .perfil(request.perfil())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CriarUsuarioRequest request,
            @RequestHeader(name = "X-Usuario-Id", required = false, defaultValue = "1") Long usuarioLogadoId) {
        exigirAdmin(usuarioLogadoId);
        UsuarioModel model = UsuarioModel.builder()
                .nome(request.nome())
                .email(request.email())
                .telefone(request.telefone())
                .setor(request.setor())
                .perfil(request.perfil())
                .build();
        return ResponseEntity.ok(service.atualizar(id, model));
    }
}

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final IUsuarioService usuarioService;

    AuthController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@Valid @RequestBody UsuarioController.LoginRequest request) {
        UsuarioDTO usuario = usuarioService.autenticar(request.email(), request.senha());
        return ResponseEntity.ok()
                .header("X-Usuario-Id", String.valueOf(usuario.getId()))
                .header("X-Usuario-Perfil", usuario.getPerfil())
                .body(usuario);
    }
}
