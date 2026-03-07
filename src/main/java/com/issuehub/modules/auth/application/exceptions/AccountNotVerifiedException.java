package com.issuehub.modules.auth.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class AccountNotVerifiedException extends ApplicationException {

    public AccountNotVerifiedException(String message) {
        super(message);
    }

}
