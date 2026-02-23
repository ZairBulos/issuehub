package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class LoginCodeAlreadyUsedException extends ApplicationException {

    public LoginCodeAlreadyUsedException(String message) {
        super(message);
    }

}
