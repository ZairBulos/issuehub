package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LoginCodeTest {

    @Test
    void shouldGenerateValidCode() {
        // When
        var code = LoginCode.generate();

        // Then
        assertThat(code.value()).isNotNull();
        assertThat(code.value().length()).isEqualTo(6);
    }

    @Test
    void shouldGenerateUniqueCodes() {
        // When
        var code1 = LoginCode.generate();
        var code2 = LoginCode.generate();

        // Then
        assertThat(code1.value()).isNotEqualTo(code2.value());
    }

    @Test
    void shouldTransformToHashed() {
        // Given
        var code = new LoginCode("123456");
        Function<String, String> dummyHasher = plain -> "hashed-" + plain;

        // When
        var hashedCode = code.toHashed(dummyHasher);

        // Then
        assertThat(hashedCode.value()).isEqualTo("hashed-123456");
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThatThrownBy(() -> new LoginCode(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenHasherIsNull() {
        // Given
        var code = LoginCode.generate();

        // When/Then
        assertThatThrownBy(() -> code.toHashed(null))
                .isInstanceOf(NullPointerException.class);
    }

}
