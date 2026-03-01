package com.issuehub.modules.auth.domain.models.valueobjects;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record RefreshTokenExpiration(Instant value) {

    private static final Duration DEFAULT_TTL = Duration.ofDays(7);

    public RefreshTokenExpiration {
        Objects.requireNonNull(value, "Refresh token expiration cannot be null");
    }

    public static RefreshTokenExpiration generate() {
        return new RefreshTokenExpiration(Instant.now().plus(DEFAULT_TTL));
    }

    public static RefreshTokenExpiration generate(Duration ttl) {
        Objects.requireNonNull(ttl, "TTL cannot be null");
        return new RefreshTokenExpiration(Instant.now().plus(ttl));
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(value);
    }

}
