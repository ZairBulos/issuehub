package com.issuehub.modules.auth.domain.models.valueobjects;

import java.util.Objects;

public record RefreshToken(String value) {

    public RefreshToken {
        Objects.requireNonNull(value, "Refresh token cannot be null");
    }

    public HashedRefreshToken toHashed() {
        return HashedRefreshToken.of(value);
    }

}
