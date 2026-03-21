package com.issuehub.modules.integrations.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class InvalidOAuthConnectionException extends DomainException {

    public InvalidOAuthConnectionException(String message) {
        super(message);
    }

}
