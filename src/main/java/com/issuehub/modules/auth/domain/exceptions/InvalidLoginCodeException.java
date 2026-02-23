package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class InvalidLoginCodeException extends ApplicationException {

    public InvalidLoginCodeException(String message) {
        super(message);
    }

}
