package com.issuehub.modules.auth.domain.models.valueobjects;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RefreshTokenExpirationTest {

    @Test
    void shouldCreateWithDefaultTTL() {
        // Given
        var before = Instant.now();

        // When
        var expiration = RefreshTokenExpiration.generate();

        // Then
        assertThat(expiration.value()).isAfterOrEqualTo(before.plus(Duration.ofDays(7)));
    }

    @Test
    void shouldCreateWithCustomTTL() {
        // Given
        var customTtl = Duration.ofDays(14);
        var before = Instant.now();

        // When
        var expiration = RefreshTokenExpiration.generate(customTtl);

        // Then
        Assertions.assertThat(expiration.value()).isAfterOrEqualTo(before.plus(customTtl));
    }

    @Test
    void shouldNotBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-02-28T10:00:00Z");
        var expiration = new RefreshTokenExpiration(fixedNow.plus(Duration.ofDays(7)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void shouldBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-02-28T10:00:00Z");
        var expiration = new RefreshTokenExpiration(fixedNow.minus(Duration.ofDays(7)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new RefreshTokenExpiration(null))
                .isInstanceOf(NullPointerException.class);
    }

}
