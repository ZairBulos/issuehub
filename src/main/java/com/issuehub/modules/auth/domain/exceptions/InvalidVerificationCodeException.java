package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class InvalidVerificationCodeException extends DomainException {

    public InvalidVerificationCodeException(String message) {
        super(message);
    }

}
