package com.issuehub.modules.integrations.domain.models.valueobjects;

import java.time.Instant;
import java.util.Objects;

public record OAuthTokenExpiration(Instant value) {

    public OAuthTokenExpiration {
        Objects.requireNonNull(value, "OAuth token expiration cannot be null");
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(value);
    }

}
