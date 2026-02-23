package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.function.BiPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashedLoginCodeTest {

    @Test
    void shouldMatchWithCorrectCode() {
        // Given
        var hashed = new HashedLoginCode("stored-hash-123456");
        var plainInput = "123456";

        BiPredicate<String, String> mockEncoder = (plain, hash) ->
                plain.equals("123456") && hash.equals("stored-hash-123456");

        // When
        var matches = hashed.matches(plainInput, mockEncoder);

        // Then
        assertThat(matches).isTrue();
    }

    @Test
    void shouldNotMatchWithIncorrectCode() {
        // Given
        var hashed = new HashedLoginCode("stored-hash-123456");
        var plainInput = "654321";

        BiPredicate<String, String> mockEncoder = (plain, hash) -> false;

        // When
        var matches = hashed.matches(plainInput, mockEncoder);

        // Then
        assertThat(matches).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new HashedLoginCode(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenEncoderIsNull() {
        // Given
        var hashed = new HashedLoginCode("any-hash");

        // When/Then
        assertThatThrownBy(() -> hashed.matches("any-code", null))
                .isInstanceOf(NullPointerException.class);
    }

}
