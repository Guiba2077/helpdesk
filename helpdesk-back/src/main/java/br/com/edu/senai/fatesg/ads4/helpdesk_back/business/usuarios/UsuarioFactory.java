package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

/**
 * Padrão Factory Method (inspirado em ClienteFactory do AV-CAR)
 * para criação de objetos de domínio Usuario conforme o perfil.
 */
public final class UsuarioFactory {

    private UsuarioFactory() {
    }

    public static Usuario criar(
            String perfilTexto,
            Long id,
            String nome,
            String email,
            String telefone,
            String setor,
            boolean ativo) {
        PerfilUsuario perfil = PerfilUsuario.fromString(perfilTexto);
        return new Usuario(id, nome, email, telefone, setor, perfil, ativo);
    }

    public static Usuario fromModel(UsuarioModel model) {
        if (model == null) {
            return null;
        }
        return criar(
                model.getPerfil(),
                model.getId(),
                model.getNome(),
                model.getEmail(),
                model.getTelefone(),
                model.getSetor(),
                model.isAtivo());
    }
}

