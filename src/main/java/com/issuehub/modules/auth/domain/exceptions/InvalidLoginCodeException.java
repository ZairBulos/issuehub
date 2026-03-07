package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class InvalidLoginCodeException extends DomainException {

    public InvalidLoginCodeException(String message) {
        super(message);
    }

}
