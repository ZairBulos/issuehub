package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class LoginCodeExpiredException extends ApplicationException {

    public LoginCodeExpiredException(String message) {
        super(message);
    }

}
