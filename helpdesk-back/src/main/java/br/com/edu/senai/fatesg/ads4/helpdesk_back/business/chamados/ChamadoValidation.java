package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.chamados;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.validations.GenericValidation;
import org.springframework.stereotype.Component;

@Component
public class ChamadoValidation extends GenericValidation<ChamadoModel> implements IChamadoValidation {

    @Override
    public void validar(ChamadoModel model) {
        naoNulo(model, "Chamado");
        naoVazio(model.getTitulo(), "Título do chamado");
        tamanhoMaximo(model.getTitulo(), 160, "Título do chamado");
        naoVazio(model.getDescricao(), "Descrição do problema");
        idValido(model.getSolicitanteId(), "Solicitante do chamado");
    }
}

