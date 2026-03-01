package com.issuehub.modules.auth.domain.models.valueobjects;

import com.issuehub.modules.auth.domain.exceptions.InvalidUserAgentException;

import java.util.Objects;

public record UserAgent(String value) {

    private static final int MAX_LENGTH = 512;

    public UserAgent {
        Objects.requireNonNull(value, "User-Agent cannot be null");

        if (value.isBlank())
            throw new InvalidUserAgentException("User-Agent must not be blank");
        if (value.length() > MAX_LENGTH)
            throw new InvalidUserAgentException("User-Agent exceeds max length of " + MAX_LENGTH);
        if (value.equalsIgnoreCase("unknown") || value.equalsIgnoreCase("null"))
            throw new InvalidUserAgentException("User-Agent value is not allowed");
    }

}
