package com.issuehub.modules.auth.domain.models.valueobjects;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record VerificationExpiration(Instant value) {

    private static final Duration DEFAULT_TTL = Duration.ofHours(24);

    public VerificationExpiration {
        Objects.requireNonNull(value, "Verification expiration cannot be null");
    }

    public static VerificationExpiration generate() {
        return new VerificationExpiration(Instant.now().plus(DEFAULT_TTL));
    }

    public static VerificationExpiration generate(Duration ttl) {
        Objects.requireNonNull(ttl, "TTL cannot be null");
        return new VerificationExpiration(Instant.now().plus(ttl));
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(value);
    }

}
