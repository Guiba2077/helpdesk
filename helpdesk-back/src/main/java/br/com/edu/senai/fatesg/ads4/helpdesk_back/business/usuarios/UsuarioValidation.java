package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.validations.GenericValidation;
import org.springframework.stereotype.Component;

@Component
public class UsuarioValidation extends GenericValidation<UsuarioModel> implements IUsuarioValidation {

    @Override
    public void validar(UsuarioModel model) {
        naoNulo(model, "Usuário");
        naoVazio(model.getNome(), "Nome do usuário");
        tamanhoMaximo(model.getNome(), 120, "Nome do usuário");
        validarEmail(model.getEmail());
        naoVazio(model.getPerfil(), "Perfil do usuário (ADMIN, TECNICO, OPERADOR)");
    }
}

