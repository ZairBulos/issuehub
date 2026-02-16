package com.issuehub.modules.developers.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class DeveloperAlreadyExistsException extends ApplicationException {

    public DeveloperAlreadyExistsException(String message) {
        super(message);
    }

}
