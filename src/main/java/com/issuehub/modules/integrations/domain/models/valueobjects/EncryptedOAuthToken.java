package com.issuehub.modules.integrations.domain.models.valueobjects;

import com.issuehub.modules.integrations.domain.exceptions.InvalidOAuthConnectionException;

import java.util.Objects;

public record EncryptedOAuthToken(String value) {

    public EncryptedOAuthToken {
        Objects.requireNonNull(value, "Hashed encrypted oauth token cannot be null");

        if (value.isBlank())
            throw new InvalidOAuthConnectionException("OAuth token cannot be blank");
    }

}
