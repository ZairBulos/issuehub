package com.issuehub.modules.integrations.application.services;

import com.issuehub.modules.integrations.application.dto.GitHubRefreshedTokenDto;
import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.domain.models.valueobjects.EncryptedOAuthToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubRefreshTokenServiceTest {

    @Mock
    private GitHubApiPort gitHubApiPort;

    @Mock
    private OAuthConnectionRepositoryPort repositoryPort;

    @Mock
    private EncryptionPort encryptionPort;

    @InjectMocks
    private GitHubRefreshTokenService gitHubRefreshTokenService;

    @Test
    void shouldRefreshTokens() {
        // Given
        var connection = mock(OAuthConnection.class);

        when(connection.isAccessTokenExpired(any())).thenReturn(true);
        when(connection.getEncryptedRefreshToken()).thenReturn(new EncryptedOAuthToken("encrypted-refresh"));
        when(encryptionPort.decrypt("encrypted-refresh")).thenReturn("raw-refresh-token");

        when(gitHubApiPort.refreshToken("raw-refresh-token")).thenReturn(new GitHubRefreshedTokenDto(
                "ghu_newaccesstoken",
                "ghr_newrefreshtoken",
                Instant.now().plusSeconds(28800),
                Instant.now().plusSeconds(15897600)
        ));
        when(encryptionPort.encrypt("ghu_newaccesstoken")).thenReturn("encrypted-new-access");
        when(encryptionPort.encrypt("ghr_newrefreshtoken")).thenReturn("encrypted-new-refresh");

        // When
        gitHubRefreshTokenService.execute(connection);

        // Then
        verify(gitHubApiPort, times(1)).refreshToken("raw-refresh-token");
        verify(encryptionPort, times(2)).encrypt(anyString());
        verify(connection, times(1)).refreshTokens(any(), any(), any(), any());
        verify(repositoryPort, times(1)).save(connection);
    }

    @Test
    void shouldDoNothingWhenAccessTokenIsNotExpired() {
        // Given
        var connection = mock(OAuthConnection.class);

        when(connection.isAccessTokenExpired(any())).thenReturn(false);

        // When
        gitHubRefreshTokenService.execute(connection);

        // Then
        verifyNoInteractions(gitHubApiPort, encryptionPort, repositoryPort);
    }

    @Test
    void shouldThrowExceptionWhenGitHubApiFailsRefreshToken() {
        // Given
        var connection = mock(OAuthConnection.class);

        when(connection.isAccessTokenExpired(any())).thenReturn(true);
        when(connection.getEncryptedRefreshToken()).thenReturn(new EncryptedOAuthToken("encrypted-refresh"));
        when(encryptionPort.decrypt("encrypted-refresh")).thenReturn("raw-refresh-token");

        when(gitHubApiPort.refreshToken("raw-refresh-token"))
                .thenThrow(new GitHubApiException("Failed to refresh token from GitHub"));

        // When/Then
        assertThatThrownBy(() -> gitHubRefreshTokenService.execute(connection))
                .isInstanceOf(GitHubApiException.class);

        verify(repositoryPort, never()).save(any());
    }

}
