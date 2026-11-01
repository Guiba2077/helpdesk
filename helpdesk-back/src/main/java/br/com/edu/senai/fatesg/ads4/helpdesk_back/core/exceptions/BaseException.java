package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final Severity severity;

    protected BaseException(String message, Severity severity) {
        super(message);
        this.severity = severity;
    }

    protected BaseException(String message, Throwable cause, Severity severity) {
        super(message, cause);
        this.severity = severity;
    }
}

