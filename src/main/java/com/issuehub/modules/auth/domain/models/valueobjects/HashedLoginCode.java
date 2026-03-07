package com.issuehub.modules.auth.domain.models.valueobjects;

import java.util.Objects;
import java.util.function.BiPredicate;

public record HashedLoginCode(String value) {

    public HashedLoginCode {
        Objects.requireNonNull(value, "Hashed login code cannot be null");
    }

    public boolean matches(String plainCode, BiPredicate<String, String> encoder) {
        Objects.requireNonNull(plainCode, "Plain code cannot be null");
        Objects.requireNonNull(encoder, "Hashed verification code encoder cannot be null");

        return encoder.test(plainCode, value);
    }

}
