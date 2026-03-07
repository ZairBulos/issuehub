package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginCodeExpirationTest {

    @Test
    void shouldCreateWithDefaultTTL() {
        // Given
        var before = Instant.now();

        // When
        var expiration = LoginCodeExpiration.generate();

        // Then
        assertThat(expiration.value()).isAfterOrEqualTo(before.plus(Duration.ofMinutes(15)));
    }

    @Test
    void shouldCreateWithCustomTTL() {
        // Given
        var customTtl = Duration.ofMinutes(30);
        var before = Instant.now();

        // When
        var expiration = LoginCodeExpiration.generate(customTtl);

        // Then
        assertThat(expiration.value()).isAfterOrEqualTo(before.plus(customTtl));
    }

    @Test
    void shouldNotBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-02-23T10:00:00Z");
        var expiration = new LoginCodeExpiration(fixedNow.plus(Duration.ofHours(1)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldBeExpired() {
        // Given
        var fixedNow = Instant.parse("2026-02-23T10:00:00Z");
        var expiration = new LoginCodeExpiration(fixedNow.minus(Duration.ofHours(1)));

        // When
        var result = expiration.isExpired(fixedNow);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThatThrownBy(() -> new LoginCodeExpiration(null))
                .isInstanceOf(NullPointerException.class);
    }

}
