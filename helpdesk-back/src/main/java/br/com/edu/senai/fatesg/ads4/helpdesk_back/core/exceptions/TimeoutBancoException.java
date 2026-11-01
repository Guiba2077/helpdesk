package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions;

public class TimeoutBancoException extends BaseException {

    public TimeoutBancoException(String message, Throwable cause) {
        super(message, cause, Severity.CRITICAL);
    }

    public TimeoutBancoException(String message) {
        super(message, Severity.CRITICAL);
    }
}

