package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions;

public class FieldValidationException extends BaseException {

    public FieldValidationException(String message) {
        super(message, Severity.WARNING);
    }
}

