package com.issuehub.modules.auth.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class ActiveLoginCodeNotFoundException extends ApplicationException {

    public ActiveLoginCodeNotFoundException(String message) {
        super(message);
    }

}
