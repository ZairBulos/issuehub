package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class LoginCodeAlreadyUsedException extends DomainException {

    public LoginCodeAlreadyUsedException(String message) {
        super(message);
    }

}
