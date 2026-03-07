package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashedRefreshTokenTest {

    @Test
    void shouldCreateHashedRefreshToken() {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                new HashedRefreshToken("some-hash")
        );
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new HashedRefreshToken(null))
                .isInstanceOf(NullPointerException.class);
    }

}
