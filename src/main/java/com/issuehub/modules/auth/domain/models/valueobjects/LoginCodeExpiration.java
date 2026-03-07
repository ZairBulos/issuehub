package com.issuehub.modules.auth.domain.models.valueobjects;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record LoginCodeExpiration(Instant value) {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(15);

    public LoginCodeExpiration {
        Objects.requireNonNull(value, "Login code expiration cannot be null");
    }

    public static LoginCodeExpiration generate() {
        return new LoginCodeExpiration(Instant.now().plus(DEFAULT_TTL));
    }

    public static LoginCodeExpiration generate(Duration ttl) {
        Objects.requireNonNull(ttl, "TTL cannot be null");
        return new LoginCodeExpiration(Instant.now().plus(ttl));
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(value);
    }

}
