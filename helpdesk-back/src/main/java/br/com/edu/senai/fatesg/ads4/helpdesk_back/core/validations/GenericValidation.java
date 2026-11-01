package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.validations;

import br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions.FieldValidationException;
import java.util.regex.Pattern;

public abstract class GenericValidation<T> implements IGenericValidation<T> {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    protected void naoNulo(Object valor, String nomeCampo) {
        if (valor == null) {
            throw new FieldValidationException("O campo '" + nomeCampo + "' é obrigatório.");
        }
    }

    protected void naoVazio(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new FieldValidationException("O campo '" + nomeCampo + "' não pode ser vazio.");
        }
    }

    protected void tamanhoMaximo(String valor, int max, String nomeCampo) {
        if (valor != null && valor.length() > max) {
            throw new FieldValidationException(
                    "O campo '" + nomeCampo + "' deve ter no máximo " + max + " caracteres.");
        }
    }

    protected void idValido(Long id, String nomeCampo) {
        if (id == null || id <= 0) {
            throw new FieldValidationException("O identificador '" + nomeCampo + "' deve ser válido e positivo.");
        }
    }

    protected void validarEmail(String email) {
        naoVazio(email, "E-mail");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new FieldValidationException("E-mail informado é inválido: " + email);
        }
    }
}

