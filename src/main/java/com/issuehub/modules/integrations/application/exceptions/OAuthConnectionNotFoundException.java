package com.issuehub.modules.integrations.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class OAuthConnectionNotFoundException extends ApplicationException {

    public OAuthConnectionNotFoundException(String message) {
        super(message);
    }

}
