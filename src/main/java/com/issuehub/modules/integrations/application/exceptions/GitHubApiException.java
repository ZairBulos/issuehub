package com.issuehub.modules.integrations.application.exceptions;

import com.issuehub.shared.application.exceptions.ApplicationException;

public class GitHubApiException extends ApplicationException {

    public GitHubApiException(String message) {
        super(message);
    }

}
