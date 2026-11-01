package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.services.IGenericService;
import java.util.List;

public interface IUsuarioService extends IGenericService<UsuarioDTO> {

    UsuarioDTO autenticar(String email, String senha);

    UsuarioDTO salvar(UsuarioModel model);

    UsuarioDTO atualizar(Long id, UsuarioModel model);

    List<UsuarioDTO> buscarPorNome(String nome);
}

