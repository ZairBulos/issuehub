package com.issuehub.shared.infrastructure.adapters.security;

import com.issuehub.shared.application.exceptions.InvalidTokenException;
import com.issuehub.shared.infrastructure.config.security.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "secret-that-is-long-enough-for-hmac256";
    private static final String ISSUER = "test";
    private static final String AUDIENCE = "test-api";

    private static final String SUBJECT = "jwt@example.com";
    private static final Map<String, Object> CLAIMS = Map.of("role", "DEVELOPER");

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setup() {
        var properties = new JwtProperties(
                SECRET,
                ISSUER,
                AUDIENCE,
                Duration.ofMinutes(15),
                Duration.ofDays(7)
        );

        tokenProvider = new JwtTokenProvider(properties);
        tokenProvider.init();
    }

    // === generation ===
    @Nested
    class GenerateAccessToken {

        @Test
        void shouldGenerateAccessToken() {
            // When
            var token = tokenProvider.generateAccessToken(SUBJECT, CLAIMS);

            // Then
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3);
        }

    }

    @Nested
    class GenerateRefreshToken {

        @Test
        void shouldGenerateRefreshToken() {
            // When
            var token = tokenProvider.generateRefreshToken(SUBJECT, CLAIMS);

            // Then
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3);
        }

    }

    // === verification ===
    @Nested
    class VerifyAccessToken {

        @Test
        void shouldReturnDecodedToken() {
            // Given
            var token = tokenProvider.generateAccessToken(SUBJECT, CLAIMS);

            // When
            var decoded = tokenProvider.verifyAccessToken(token);

            // Then
            assertThat(decoded.jti()).isNotBlank();
            assertThat(decoded.subject()).isEqualTo(SUBJECT);
            assertThat(decoded.claims()).containsEntry("role", "DEVELOPER");
            assertThat(decoded.issuedAt()).isNotNull();
            assertThat(decoded.expiresAt()).isNotNull();
        }

        @Test
        void shouldThrowExceptionWhenMalformedToken() {
            // When/Then
            assertThatThrownBy(() -> tokenProvider.verifyAccessToken("not.a.jwt"))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExceptionWhenRefreshTokenUsedAsAccessToken() {
            // Given
            var refreshToken = tokenProvider.generateRefreshToken(SUBJECT, CLAIMS);

            // When/Then
            assertThatThrownBy(() -> tokenProvider.verifyAccessToken(refreshToken))
                    .isInstanceOf(InvalidTokenException.class);
        }

    }

    @Nested
    class VerifyRefreshToken {

        @Test
        void shouldReturnDecodedToken() {
            // Given
            var token = tokenProvider.generateRefreshToken(SUBJECT, CLAIMS);

            // When
            var decoded = tokenProvider.verifyRefreshToken(token);

            // Then
            assertThat(decoded.jti()).isNotBlank();
            assertThat(decoded.subject()).isEqualTo(SUBJECT);
            assertThat(decoded.claims()).containsEntry("role", "DEVELOPER");
            assertThat(decoded.issuedAt()).isNotNull();
            assertThat(decoded.expiresAt()).isNotNull();
        }

        @Test
        void shouldThrowExceptionWhenMalformedToken() {
            // When/Then
            assertThatThrownBy(() -> tokenProvider.verifyRefreshToken("not.a.jwt"))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExceptionWhenAccessTokenUsedAsRefreshToken() {
            // Given
            var accessToken = tokenProvider.generateAccessToken(SUBJECT, CLAIMS);

            // When/Then
            assertThatThrownBy(() -> tokenProvider.verifyRefreshToken(accessToken))
                    .isInstanceOf(InvalidTokenException.class);
        }

    }

}
