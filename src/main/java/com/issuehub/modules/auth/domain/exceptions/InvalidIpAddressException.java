package com.issuehub.modules.auth.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;

public class InvalidIpAddressException extends DomainException {

    public InvalidIpAddressException(String message) {
        super(message);
    }

}
