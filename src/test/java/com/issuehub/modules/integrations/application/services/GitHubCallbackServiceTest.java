package com.issuehub.modules.integrations.application.services;

import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.modules.integrations.application.dto.GitHubCallbackCommand;
import com.issuehub.modules.integrations.application.dto.GitHubAccountDto;
import com.issuehub.modules.integrations.application.exceptions.AccountBlockedException;
import com.issuehub.modules.integrations.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubCallbackServiceTest {

    @Mock
    private GitHubApiPort gitHubApiPort;

    @Mock
    private OAuthConnectionRepositoryPort repositoryPort;

    @Mock
    private FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;

    @Mock
    private EncryptionPort encryptionPort;

    @InjectMocks
    private GitHubCallbackService gitHubCallbackService;

    private static final String CODE = "github-code-123";
    private static final String EMAIL = "dev@example.com";

    private GitHubCallbackCommand command() {
        return new GitHubCallbackCommand(CODE, EMAIL);
    }

    private DeveloperView activeDeveloper() {
        return new DeveloperView(EntityId.generate(), EMAIL, true, "active");
    }

    private DeveloperView blockedDeveloper() {
        return new DeveloperView(EntityId.generate(), EMAIL, false, "blocked");
    }

    @Test
    void shouldSaveOAuthConnection() {
        // Given
        var developer = activeDeveloper();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(developer));
        when(gitHubApiPort.getAccount(CODE)).thenReturn(new GitHubAccountDto(
                "12345678",
                "test",
                "ghu_accesstoken",
                "ghr_refreshtoken",
                Instant.now().plusSeconds(28800),
                Instant.now().plusSeconds(15897600)
        ));
        when(encryptionPort.encrypt("ghu_accesstoken")).thenReturn("encrypted-access");
        when(encryptionPort.encrypt("ghr_refreshtoken")).thenReturn("encrypted-refresh");

        // When
        gitHubCallbackService.execute(command());

        // Then
        verify(gitHubApiPort, times(1)).getAccount(CODE);
        verify(encryptionPort, times(2)).encrypt(anyString());
        verify(repositoryPort, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDeveloperNotFound() {
        // Given
        var cmd = command();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> gitHubCallbackService.execute(cmd))
                .isInstanceOf(AccountNotFoundException.class);

        verifyNoInteractions(gitHubApiPort, encryptionPort, repositoryPort);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperIsBlocked() {
        // Given
        var cmd = command();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(blockedDeveloper()));

        // When/Then
        assertThatThrownBy(() -> gitHubCallbackService.execute(cmd))
                .isInstanceOf(AccountBlockedException.class);

        verifyNoInteractions(gitHubApiPort, encryptionPort, repositoryPort);
    }

    @Test
    void shouldThrowExceptionWhenGitHubApiFailsAccessToken() {
        // Given
        var cmd = command();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(activeDeveloper()));
        when(gitHubApiPort.getAccount(CODE)).thenThrow(new GitHubApiException("Failed to obtain access token from GitHub"));

        // When/Then
        assertThatThrownBy(() -> gitHubCallbackService.execute(cmd))
                .isInstanceOf(GitHubApiException.class);

        verifyNoInteractions(encryptionPort, repositoryPort);
    }

}
