package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions;

public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(message, Severity.WARNING);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause, Severity.WARNING);
    }
}

