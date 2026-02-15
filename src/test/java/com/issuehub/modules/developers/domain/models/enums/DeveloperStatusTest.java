package com.issuehub.modules.developers.domain.models.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeveloperStatusTest {

    @Test
    void shouldExposeStringValue() {
        assertThat(DeveloperStatus.ACTIVE.value())
                .isEqualTo("active");
        assertThat(DeveloperStatus.BLOCKED.value())
                .isEqualTo("blocked");
        assertThat(DeveloperStatus.DELETED.value())
                .isEqualTo("deleted");
    }

    @Test
    void shouldMapFromValidStringValue() {
        assertThat(DeveloperStatus.fromValue("active"))
                .isEqualTo(DeveloperStatus.ACTIVE);
        assertThat(DeveloperStatus.fromValue("blocked"))
                .isEqualTo(DeveloperStatus.BLOCKED);
        assertThat(DeveloperStatus.fromValue("deleted"))
                .isEqualTo(DeveloperStatus.DELETED);
    }

    @Test
    void shouldThrowExceptionForInvalidStringValue() {
        assertThatThrownBy(() -> DeveloperStatus.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
