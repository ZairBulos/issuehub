package com.issuehub.modules.integrations.domain.models.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuthProviderTest {

    @Test
    void shouldExposeStringValue() {
        // When/Then
        assertThat(OAuthProvider.GITHUB.value()).isEqualTo("github");
        assertThat(OAuthProvider.GITLAB.value()).isEqualTo("gitlab");
    }

    @Test
    void shouldMapFromValidStringValue() {
        // When/Then
        assertThat(OAuthProvider.fromValue("github")).isEqualTo(OAuthProvider.GITHUB);
        assertThat(OAuthProvider.fromValue("gitlab")).isEqualTo(OAuthProvider.GITLAB);
    }

    @Test
    void shouldThrowExceptionForInvalidStringValue() {
        // When/Then
        assertThatThrownBy(() -> OAuthProvider.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
