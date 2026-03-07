package com.issuehub.modules.auth.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class AccountNotFoundException extends ApplicationException {

    public AccountNotFoundException(String message) {
        super(message);
    }

}
