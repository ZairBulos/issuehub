package com.issuehub.modules.integrations.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class EncryptionException extends ApplicationException {

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

}
