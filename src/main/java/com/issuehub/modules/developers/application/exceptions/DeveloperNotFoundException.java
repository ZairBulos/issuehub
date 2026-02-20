package com.issuehub.modules.developers.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class DeveloperNotFoundException extends ApplicationException {

    public DeveloperNotFoundException(String message) {
        super(message);
    }

}
