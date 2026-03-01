package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class InvalidUserAgentException extends DomainException {

    public InvalidUserAgentException(String message) {
        super(message);
    }

}
