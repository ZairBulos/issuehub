package com.issuehub.modules.developers.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class InvalidDeveloperEmailException extends DomainException {

    public InvalidDeveloperEmailException(String message) {
        super(message);
    }

}
