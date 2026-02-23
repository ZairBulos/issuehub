package com.issuehub.modules.auth.domain.models.valueobjects;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public record LoginCode(String value) {

    public LoginCode {
        Objects.requireNonNull(value, "Login code cannot be null");
    }

    public static LoginCode generate() {
        var random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return new LoginCode(String.valueOf(random));
    }

    public HashedLoginCode toHashed(Function<String, String> hasher) {
        Objects.requireNonNull(hasher, "Hasher cannot be null");

        return new HashedLoginCode(hasher.apply(value));
    }

}
