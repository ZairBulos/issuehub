package com.issuehub.modules.auth.domain.models.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenTest {

    @Test
    void shouldHashDeterministically() {
        // Given
        var refreshToken = new RefreshToken("""
            eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
            .eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0
            .KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30
            """
        );

        // When
        var hash1 = refreshToken.toHashed();
        var hash2 = refreshToken.toHashed();

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void shouldProduceDifferentHashesForDifferentTokens() {
        // Given
        var refreshToken1 = new RefreshToken("token-a");
        var refreshToken2=  new RefreshToken("token-b");

        // When
        var hash1 = refreshToken1.toHashed();
        var hash2 = refreshToken2.toHashed();

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new RefreshToken(null))
                .isInstanceOf(NullPointerException.class);
    }

}
