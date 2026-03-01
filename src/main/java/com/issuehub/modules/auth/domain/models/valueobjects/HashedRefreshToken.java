package com.issuehub.modules.auth.domain.models.valueobjects;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public record HashedRefreshToken(String value) {

    private static final String ALGORITHM = "SHA-256";

    public HashedRefreshToken {
        Objects.requireNonNull(value, "Hashed refresh token cannot be null");
    }

    public static HashedRefreshToken of(String plain) {
        try {
            var digest = MessageDigest.getInstance(ALGORITHM);
            var bytes = digest.digest(plain.getBytes(StandardCharsets.UTF_8));
            return new HashedRefreshToken(HexFormat.of().formatHex(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithm not available", e);
        }
    }

}
