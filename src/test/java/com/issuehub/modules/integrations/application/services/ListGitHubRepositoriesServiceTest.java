package com.issuehub.modules.integrations.application.services;

import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.modules.integrations.application.dto.GitHubRepositoryDto;
import com.issuehub.modules.integrations.application.dto.ListGitHubRepositoriesQuery;
import com.issuehub.modules.integrations.application.exceptions.AccountBlockedException;
import com.issuehub.modules.integrations.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.application.exceptions.OAuthConnectionNotFoundException;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.domain.models.enums.OAuthProvider;
import com.issuehub.modules.integrations.domain.models.valueobjects.EncryptedOAuthToken;
import com.issuehub.modules.integrations.domain.models.valueobjects.ProviderUsername;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListGitHubRepositoriesServiceTest {

    @Mock
    private GitHubApiPort gitHubApiPort;

    @Mock
    private OAuthConnectionRepositoryPort repositoryPort;

    @Mock
    private FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;

    @Mock
    private GitHubRefreshTokenService gitHubRefreshTokenService;

    @Mock
    private EncryptionPort encryptionPort;

    @InjectMocks
    private ListGitHubRepositoriesService listGitHubRepositoriesService;

    private static final String EMAIL = "dev@example.com";
    private static final String PROVIDER_USER_ID = "12345678";
    private static final String RAW_ACCESS_TOKEN = "ghu_accesstoken";
    private static final String ENCRYPTED_ACCESS_TOKEN = "encrypted-access";


    private ListGitHubRepositoriesQuery query() {
        return new ListGitHubRepositoriesQuery(EMAIL, PROVIDER_USER_ID, 1, 30);
    }

    private DeveloperView activeDeveloper() {
        return new DeveloperView(EntityId.generate(), EMAIL, true, "active");
    }

    private DeveloperView blockedDeveloper() {
        return new DeveloperView(EntityId.generate(), EMAIL, false, "blocked");
    }

    private OAuthConnection connection() {
        var connection = mock(OAuthConnection.class);
        when(connection.getEncryptedAccessToken()).thenReturn(new EncryptedOAuthToken(ENCRYPTED_ACCESS_TOKEN));
        when(connection.getProviderUsername()).thenReturn(new ProviderUsername("octocat"));
        return connection;
    }

    @Test
    void shouldReturnRepositories() {
        // Given
        var developer = activeDeveloper();
        var connection = connection();
        var repositories = List.of(
                new GitHubRepositoryDto(1L, "repo", "octocat/repo", "octocat")
        );

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(developer));
        when(repositoryPort.findByDeveloperIdAndProviderAndProviderUserId(developer.id(), OAuthProvider.GITHUB.value(), PROVIDER_USER_ID))
                .thenReturn(Optional.of(connection));
        when(encryptionPort.decrypt(ENCRYPTED_ACCESS_TOKEN)).thenReturn(RAW_ACCESS_TOKEN);
        when(gitHubApiPort.getRepositories(RAW_ACCESS_TOKEN, "octocat", 1, 30))
                .thenReturn(repositories);

        // When
        var result = listGitHubRepositoriesService.execute(query());

        // Then
        assertThat(result).hasSize(1);
        verify(gitHubRefreshTokenService, times(1)).execute(connection);
        verify(gitHubApiPort, times(1)).getRepositories(RAW_ACCESS_TOKEN, "octocat", 1, 30);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperNotFound() {
        // Given
        var query = query();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> listGitHubRepositoriesService.execute(query))
                .isInstanceOf(AccountNotFoundException.class);

        verifyNoInteractions(gitHubApiPort, encryptionPort, repositoryPort, gitHubRefreshTokenService);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperIsBlocked() {
        var query = query();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(blockedDeveloper()));

        // When/Then
        assertThatThrownBy(() -> listGitHubRepositoriesService.execute(query))
                .isInstanceOf(AccountBlockedException.class);

        verifyNoInteractions(gitHubApiPort, encryptionPort, repositoryPort, gitHubRefreshTokenService);
    }

    @Test
    void shouldThrowExceptionWhenConnectionNotFound() {
        // Given
        var query = query();
        var developer = activeDeveloper();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(developer));
        when(repositoryPort.findByDeveloperIdAndProviderAndProviderUserId(developer.id(), OAuthProvider.GITHUB.value(), PROVIDER_USER_ID))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> listGitHubRepositoriesService.execute(query))
                .isInstanceOf(OAuthConnectionNotFoundException.class);

        verifyNoInteractions(gitHubApiPort, encryptionPort, gitHubRefreshTokenService);
    }

    @Test
    void shouldThrowExceptionWhenGitHubApiFails() {
        // Given
        var query = query();
        var developer = activeDeveloper();
        var connection = connection();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(developer));
        when(repositoryPort.findByDeveloperIdAndProviderAndProviderUserId(developer.id(), OAuthProvider.GITHUB.value(), PROVIDER_USER_ID))
                .thenReturn(Optional.of(connection));
        when(encryptionPort.decrypt(ENCRYPTED_ACCESS_TOKEN)).thenReturn(RAW_ACCESS_TOKEN);
        when(gitHubApiPort.getRepositories(any(), any(), anyInt(), anyInt()))
                .thenThrow(new GitHubApiException("Failed to obtain repositories from GitHub"));

        // When/Then
        assertThatThrownBy(() -> listGitHubRepositoriesService.execute(query))
                .isInstanceOf(GitHubApiException.class);

        verify(repositoryPort, never()).save(any());
    }

}
