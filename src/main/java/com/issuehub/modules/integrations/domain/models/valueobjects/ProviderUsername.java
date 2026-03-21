package com.issuehub.modules.integrations.domain.models.valueobjects;

import com.issuehub.modules.integrations.domain.exceptions.InvalidOAuthConnectionException;

import java.util.Objects;

public record ProviderUsername(String value) {

    public ProviderUsername {
        Objects.requireNonNull(value, "Provider username cannot be null");

        if (value.isBlank())
            throw new InvalidOAuthConnectionException("Provider username cannot be blank");
    }

}
