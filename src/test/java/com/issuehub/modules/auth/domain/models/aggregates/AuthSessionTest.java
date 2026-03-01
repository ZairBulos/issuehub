package com.issuehub.modules.auth.domain.models.aggregates;

import com.issuehub.modules.auth.domain.exceptions.AuthSessionAlreadyRevokedException;
import com.issuehub.modules.auth.domain.models.valueobjects.IpAddress;
import com.issuehub.modules.auth.domain.models.valueobjects.RefreshToken;
import com.issuehub.modules.auth.domain.models.valueobjects.RefreshTokenExpiration;
import com.issuehub.modules.auth.domain.models.valueobjects.UserAgent;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthSessionTest {

    private AuthSession session;

    @BeforeEach
    void setup() {
        var developerId = EntityId.generate();
        var hashedToken = new RefreshToken("refresh-token").toHashed();
        var expiresAt = RefreshTokenExpiration.generate();
        var ip = new IpAddress("127.0.0.1");
        var userAgent = new UserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        session = AuthSession.create(developerId, hashedToken, expiresAt, ip, userAgent);
    }

    // === create ===
    @Test
    void shouldCreateAuthSession() {
        // Then
        assertThat(session.isRevoked()).isFalse();
        assertThat(session.isExpired(Instant.now())).isFalse();
        assertThat(session.isValid(Instant.now())).isTrue();
    }

    // === revoke ===
    @Test
    void shouldRevokeSession() {
        // When
        session.revoke();

        // Then
        assertThat(session.isRevoked()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenAlreadyRevoked() {
        // Given
        session.revoke();

        // When/Then
        assertThatThrownBy(session::revoke)
                .isInstanceOf(AuthSessionAlreadyRevokedException.class);
    }

}
