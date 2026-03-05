package com.issuehub.shared.application.exceptions;

public class InvalidTokenException extends ApplicationException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
