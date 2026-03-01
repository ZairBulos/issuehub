package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class AuthSessionAlreadyRevokedException extends DomainException {

    public AuthSessionAlreadyRevokedException(String message) {
        super(message);
    }

}
