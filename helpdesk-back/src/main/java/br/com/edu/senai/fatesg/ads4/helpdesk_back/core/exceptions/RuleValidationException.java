package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions;

public class RuleValidationException extends BaseException {

    public RuleValidationException(String message) {
        super(message, Severity.WARNING);
    }
}

