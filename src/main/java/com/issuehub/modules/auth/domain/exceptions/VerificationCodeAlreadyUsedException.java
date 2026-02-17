package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class VerificationCodeAlreadyUsedException extends DomainException {

    public VerificationCodeAlreadyUsedException(String message) {
        super(message);
    }

}
