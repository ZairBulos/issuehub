package com.issuehub.modules.developers.domain.models.valueobjects;

import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperEmailException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeveloperEmailTest {

    @Test
    void shouldCreateValidEmailAndNormalizeValue() {
        var email = new DeveloperEmail("  TEST@Example.com  ");
        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThatThrownBy(() -> new DeveloperEmail(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsTooLong() {
        String longEmail = "a".repeat(320) + "@example.com";
        assertThatThrownBy(() -> new DeveloperEmail(longEmail))
                .isInstanceOf(InvalidDeveloperEmailException.class);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalidFormat() {
        assertThatThrownBy(() -> new DeveloperEmail("invalid-email"))
                .isInstanceOf(InvalidDeveloperEmailException.class);
    }

}
