package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.function.BiPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashedVerificationCodeTest {

    @Test
    void shouldMatchWithCorrectCode() {
        // Given
        var hashed = new HashedVerificationCode("stored-hash-123");
        var plainInput = "correct-password";

        BiPredicate<String, String> mockEncoder = (plain, hash) ->
                plain.equals("correct-password") && hash.equals("stored-hash-123");

        // When
        var matches = hashed.matches(plainInput, mockEncoder);

        // Then
        assertThat(matches).isTrue();
    }

    @Test
    void shouldNotMatchWithIncorrectCode() {
        // Given
        var hashed = new HashedVerificationCode("stored-hash-123");
        var plainInput = "wrong-password";

        BiPredicate<String, String> mockEncoder = (plain, hash) -> false;

        // When
        var matches = hashed.matches(plainInput, mockEncoder);

        // Then
        assertThat(matches).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThatThrownBy(() -> new HashedVerificationCode(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenEncoderIsNull() {
        var hashed = new HashedVerificationCode("any-hash");

        assertThatThrownBy(() -> hashed.matches("any-code", null))
                .isInstanceOf(NullPointerException.class);
    }

}
