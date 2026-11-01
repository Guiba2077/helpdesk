package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.repositories.Repository;
import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends Repository<UsuarioModel> {

    Optional<UsuarioModel> buscarPorEmail(String email);

    Optional<UsuarioModel> autenticarComPgcrypto(String email, String senhaPura);

    List<UsuarioModel> buscarPorNome(String nome);
}

