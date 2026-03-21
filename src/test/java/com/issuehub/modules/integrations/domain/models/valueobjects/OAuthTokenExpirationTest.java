package com.issuehub.modules.integrations.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuthTokenExpirationTest {

    @Test
    void shouldNotBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-03-21T10:00:00Z");
        var expiration = new OAuthTokenExpiration(fixedNow.plus(Duration.ofHours(8)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-03-21T10:00:00Z");
        var expiration = new OAuthTokenExpiration(fixedNow.minus(Duration.ofHours(8)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new OAuthTokenExpiration(null))
                .isInstanceOf(NullPointerException.class);
    }

}
