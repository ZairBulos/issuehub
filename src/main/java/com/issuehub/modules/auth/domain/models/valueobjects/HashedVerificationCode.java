package com.issuehub.modules.auth.domain.models.valueobjects;

import java.util.Objects;
import java.util.function.BiPredicate;

public record HashedVerificationCode(String value) {

    public HashedVerificationCode {
        Objects.requireNonNull(value, "Hashed verification code cannot be null");
    }

    public boolean matches(String plainCode, BiPredicate<String, String> encoder) {
        Objects.requireNonNull(plainCode, "Plain code cannot be null");
        Objects.requireNonNull(encoder, "Hashed verification code encoder cannot be null");

        return encoder.test(plainCode, value);
    }

}
