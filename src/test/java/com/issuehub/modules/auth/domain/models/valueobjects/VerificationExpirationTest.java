package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationExpirationTest {

    @Test
    void shouldCreateWithDefaultTTL() {
        // Given
        var before = Instant.now();

        // When
        var expiration = VerificationExpiration.generate();

        // Then
        assertThat(expiration.value()).isAfterOrEqualTo(before.plus(Duration.ofHours(24)));
    }

    @Test
    void shouldCreateWithCustomTTL() {
        // Given
        var customTtl = Duration.ofMinutes(15);
        var before = Instant.now();

        // When
        var expiration = VerificationExpiration.generate(customTtl);

        // Then
        assertThat(expiration.value()).isAfterOrEqualTo(before.plus(customTtl));
    }

    @Test
    void shouldNotBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-02-16T10:00:00Z");
        var expiration = new VerificationExpiration(fixedNow.plus(Duration.ofHours(1)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-02-16T10:00:00Z");
        var expiration = new VerificationExpiration(fixedNow.minus(Duration.ofHours(1)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThatThrownBy(() -> new VerificationExpiration(null))
                .isInstanceOf(NullPointerException.class);
    }

}
