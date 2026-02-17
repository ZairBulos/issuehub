package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

class VerificationCodeTest {

    @Test
    void shouldGenerateValidCode() {
        // When
        var code = VerificationCode.generate();

        // Then
        assertThat(code.value()).isNotNull();
    }

    @Test
    void shouldGenerateUniqueCodes() {
        // When
        var code1 = VerificationCode.generate();
        var code2 = VerificationCode.generate();

        // Then
        assertThat(code1.value()).isNotEqualTo(code2.value());
    }

    @Test
    void shouldTransformToHashed() {
        // Given
        var code = new VerificationCode("plain-text-code");
        Function<String, String> dummyHasher = plain -> "hashed-" + plain;

        // When
        var hashedCode = code.toHashed(dummyHasher);

        // Then
        assertThat(hashedCode.value()).isEqualTo("hashed-plain-text-code");
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThatThrownBy(() -> new VerificationCode(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenHasherIsNull() {
        var code = VerificationCode.generate();

        assertThatThrownBy(() -> code.toHashed(null))
                .isInstanceOf(NullPointerException.class);
    }

}
