package com.issuehub.modules.developers.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class InvalidDeveloperProfileException extends DomainException {

    public InvalidDeveloperProfileException(String message) {
        super(message);
    }

}
