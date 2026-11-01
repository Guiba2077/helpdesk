package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.tecnicos;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.validations.GenericValidation;
import org.springframework.stereotype.Component;

@Component
public class TecnicoValidation extends GenericValidation<TecnicoModel> implements ITecnicoValidation {

    @Override
    public void validar(TecnicoModel model) {
        naoNulo(model, "Técnico");
        idValido(model.getUsuarioId(), "ID do Usuário vinculado ao Técnico");
        naoVazio(model.getMatricula(), "Matrícula do técnico");
        naoVazio(model.getEspecialidade(), "Especialidade do técnico");
        naoVazio(model.getNivelAtendimento(), "Nível de atendimento (N1, N2 ou N3)");
    }
}

