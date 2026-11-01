package br.com.edu.senai.fatesg.ads4.helpdesk_back.core.exceptions;

public class NegocioException extends RuntimeException {

    public NegocioException(String message) {
        super(message);
    }

    public NegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}

