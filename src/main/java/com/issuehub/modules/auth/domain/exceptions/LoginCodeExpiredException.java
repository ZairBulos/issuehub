package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class LoginCodeExpiredException extends DomainException {

    public LoginCodeExpiredException(String message) {
        super(message);
    }

}
