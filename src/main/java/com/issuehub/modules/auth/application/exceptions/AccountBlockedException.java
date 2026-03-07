package com.issuehub.modules.auth.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class AccountBlockedException extends ApplicationException {

    public AccountBlockedException(String message) {
        super(message);
    }

}
