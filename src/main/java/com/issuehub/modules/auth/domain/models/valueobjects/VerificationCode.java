package com.issuehub.modules.auth.domain.models.valueobjects;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public record VerificationCode(String value) {

    public VerificationCode {
        Objects.requireNonNull(value, "Verification code cannot be null");
    }

    public static VerificationCode generate() {
        return new VerificationCode(UUID.randomUUID().toString());
    }

    public HashedVerificationCode toHashed(Function<String, String> hasher) {
        Objects.requireNonNull(hasher, "Hasher cannot be null");

        return new HashedVerificationCode(hasher.apply(value));
    }

}
