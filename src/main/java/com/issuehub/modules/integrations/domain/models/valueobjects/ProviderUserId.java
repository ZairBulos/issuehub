package com.issuehub.modules.integrations.domain.models.valueobjects;

import com.issuehub.modules.integrations.domain.exceptions.InvalidOAuthConnectionException;

import java.util.Objects;

public record ProviderUserId(String value) {

    public ProviderUserId {
        Objects.requireNonNull(value, "Provider user ID cannot be null");

        if (value.isBlank())
            throw new InvalidOAuthConnectionException("Provider user ID cannot be blank");
    }

}
