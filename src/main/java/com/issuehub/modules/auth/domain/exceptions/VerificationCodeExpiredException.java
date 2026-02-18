package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class VerificationCodeExpiredException extends DomainException {

    public VerificationCodeExpiredException(String message) {
        super(message);
    }

}
