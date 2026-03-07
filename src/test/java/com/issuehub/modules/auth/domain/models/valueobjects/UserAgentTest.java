package com.issuehub.modules.auth.domain.models.valueobjects;

import com.issuehub.modules.auth.domain.exceptions.InvalidUserAgentException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserAgentTest {

    @Test
    void shouldCreateUserAgent() {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                new UserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        );
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new UserAgent(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenBlank() {
        // When/Then
        assertThatThrownBy(() -> new UserAgent("  "))
                .isInstanceOf(InvalidUserAgentException.class);
    }

    @Test
    void shouldThrowExceptionWhenExceedsMaxLength() {
        // Given
        String longUserAgent = "User-Agent".repeat(513);

        // When/Then
        assertThatThrownBy(() -> new UserAgent(longUserAgent))
                .isInstanceOf(InvalidUserAgentException.class);
    }

    @Test
    void shouldThrowExceptionWhenValueIsUnknown() {
        // When/Then
        assertThatThrownBy(() -> new UserAgent("unknown"))
                .isInstanceOf(InvalidUserAgentException.class);
    }

    @Test
    void shouldThrowExceptionWhenValueIsNullLiteral() {
        assertThatThrownBy(() -> new UserAgent("null"))
                .isInstanceOf(InvalidUserAgentException.class);
    }

}
