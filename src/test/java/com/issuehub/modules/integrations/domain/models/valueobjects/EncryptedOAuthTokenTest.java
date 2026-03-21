package com.issuehub.modules.integrations.domain.models.valueobjects;

import com.issuehub.modules.integrations.domain.exceptions.InvalidOAuthConnectionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EncryptedOAuthTokenTest {

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new EncryptedOAuthToken(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenBlank() {
        // When/Then
        assertThatThrownBy(() -> new EncryptedOAuthToken(""))
                .isInstanceOf(InvalidOAuthConnectionException.class);
    }

}
