package com.issuehub.modules.integrations.domain.models.aggregates;

import com.issuehub.modules.integrations.domain.models.enums.OAuthProvider;
import com.issuehub.modules.integrations.domain.models.valueobjects.EncryptedOAuthToken;
import com.issuehub.modules.integrations.domain.models.valueobjects.OAuthTokenExpiration;
import com.issuehub.modules.integrations.domain.models.valueobjects.ProviderUserId;
import com.issuehub.modules.integrations.domain.models.valueobjects.ProviderUsername;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthConnectionTest {

    private static final EntityId DEVELOPER_ID = EntityId.generate();
    private static final ProviderUserId PROVIDER_USER_ID = new ProviderUserId("12345678");
    private static final ProviderUsername PROVIDER_USERNAME = new ProviderUsername("test");
    private static final EncryptedOAuthToken ACCESS_TOKEN = new EncryptedOAuthToken("encrypted-access-toke");
    private static final EncryptedOAuthToken REFRESH_TOKEN = new EncryptedOAuthToken("encrypted-refresh-toke");
    private static final OAuthTokenExpiration ACCESS_EXPIRATION  = new OAuthTokenExpiration(Instant.now().plus(Duration.ofHours(8)));
    private static final OAuthTokenExpiration REFRESH_EXPIRATION = new OAuthTokenExpiration(Instant.now().plus(Duration.ofDays(30)));

    private OAuthConnection githubConnection() {
        return OAuthConnection.createGitHub(
                DEVELOPER_ID,
                PROVIDER_USER_ID,
                PROVIDER_USERNAME,
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                ACCESS_EXPIRATION,
                REFRESH_EXPIRATION
        );
    }

    private OAuthConnection gitlabConnection() {
        return OAuthConnection.createGitLab(
                DEVELOPER_ID,
                PROVIDER_USER_ID,
                PROVIDER_USERNAME,
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                ACCESS_EXPIRATION,
                REFRESH_EXPIRATION
        );
    }

    // === create ===
    @Test
    void shouldCreateGitHubConnection() {
        // Given
        var connection = githubConnection();

        // Then
        assertThat(connection.getId()).isNotNull();
        assertThat(connection.getDeveloperId()).isEqualTo(DEVELOPER_ID);
        assertThat(connection.getProvider()).isEqualTo(OAuthProvider.GITHUB);
        assertThat(connection.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
        assertThat(connection.getProviderUsername()).isEqualTo(PROVIDER_USERNAME);
        assertThat(connection.getEncryptedAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(connection.getEncryptedRefreshToken()).isEqualTo(REFRESH_TOKEN);
        assertThat(connection.getCreatedAt()).isNotNull();
        assertThat(connection.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldCreateGitLabConnection() {
        // Given
        var connection = gitlabConnection();

        // Then
        assertThat(connection.getId()).isNotNull();
        assertThat(connection.getDeveloperId()).isEqualTo(DEVELOPER_ID);
        assertThat(connection.getProvider()).isEqualTo(OAuthProvider.GITLAB);
        assertThat(connection.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
        assertThat(connection.getProviderUsername()).isEqualTo(PROVIDER_USERNAME);
        assertThat(connection.getEncryptedAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(connection.getEncryptedRefreshToken()).isEqualTo(REFRESH_TOKEN);
        assertThat(connection.getCreatedAt()).isNotNull();
        assertThat(connection.getUpdatedAt()).isNotNull();
    }

    // === checks ===
    @Test
    void shouldNotBeExpiredWhenAccessTokenIsValid() {
        // Given
        var connection = githubConnection();

        // When/Then
        assertThat(connection.isAccessTokenExpired(Instant.now())).isFalse();
    }

    @Test
    void shouldBeExpiredWhenAccessTokenIsExpired() {
        // Given
        var connection = OAuthConnection.createGitHub(
                DEVELOPER_ID,
                PROVIDER_USER_ID,
                PROVIDER_USERNAME,
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                new OAuthTokenExpiration(Instant.now().minusSeconds(1)),
                REFRESH_EXPIRATION
        );

        // When/Then
        assertThat(connection.isAccessTokenExpired(Instant.now())).isTrue();
    }

    @Test
    void shouldNotBeExpiredWhenRefreshTokenIsValid() {
        // Given
        var connection = gitlabConnection();

        // When/Then
        assertThat(connection.isRefreshTokenExpired(Instant.now())).isFalse();
    }

    @Test
    void shouldBeExpiredWhenRefreshTokenIsExpired() {
        // Given
        var connection = OAuthConnection.createGitLab(
                DEVELOPER_ID,
                PROVIDER_USER_ID,
                PROVIDER_USERNAME,
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                ACCESS_EXPIRATION,
                new OAuthTokenExpiration(Instant.now().minusSeconds(1))
        );

        // When/Then
        assertThat(connection.isRefreshTokenExpired(Instant.now())).isTrue();
    }

    // === actions ===
    @Test
    void shouldRefreshTokens() {
        // Given
        var connection = githubConnection();
        var updatedAt = connection.getUpdatedAt();

        var newAccess  = new EncryptedOAuthToken("new-encrypted-access-token");
        var newRefresh = new EncryptedOAuthToken("new-encrypted-refresh-token");
        var newAccessExp = new OAuthTokenExpiration(Instant.now().plus(Duration.ofHours(8)));
        var newRefreshExp = new OAuthTokenExpiration(Instant.now().plus(Duration.ofDays(30)));

        // When
        connection.refreshTokens(newAccess, newRefresh, newAccessExp, newRefreshExp);

        // Then
        assertThat(connection.getEncryptedAccessToken()).isEqualTo(newAccess);
        assertThat(connection.getEncryptedRefreshToken()).isEqualTo(newRefresh);
        assertThat(connection.getAccessTokenExpiresAt()).isEqualTo(newAccessExp);
        assertThat(connection.getRefreshTokenExpiresAt()).isEqualTo(newRefreshExp);
        assertThat(connection.getUpdatedAt()).isAfterOrEqualTo(updatedAt);
    }

}
