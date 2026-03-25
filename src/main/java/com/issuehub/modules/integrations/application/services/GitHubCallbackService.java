package com.issuehub.modules.integrations.application.services;

import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.modules.integrations.application.dto.GitHubCallbackCommand;
import com.issuehub.modules.integrations.application.exceptions.AccountBlockedException;
import com.issuehub.modules.integrations.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.integrations.application.ports.in.GitHubCallbackUseCase;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.domain.models.valueobjects.EncryptedOAuthToken;
import com.issuehub.modules.integrations.domain.models.valueobjects.OAuthTokenExpiration;
import com.issuehub.modules.integrations.domain.models.valueobjects.ProviderUserId;
import com.issuehub.modules.integrations.domain.models.valueobjects.ProviderUsername;

public class GitHubCallbackService implements GitHubCallbackUseCase {

    private final GitHubApiPort gitHubApiPort;
    private final OAuthConnectionRepositoryPort repositoryPort;
    private final FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;
    private final EncryptionPort encryptionPort;

    public GitHubCallbackService(
            GitHubApiPort gitHubApiPort,
            OAuthConnectionRepositoryPort repositoryPort,
            FindDeveloperByEmailUseCase findDeveloperByEmailUseCase,
            EncryptionPort encryptionPort
    ) {
        this.gitHubApiPort = gitHubApiPort;
        this.repositoryPort = repositoryPort;
        this.findDeveloperByEmailUseCase = findDeveloperByEmailUseCase;
        this.encryptionPort = encryptionPort;
    }

    @Override
    public void execute(GitHubCallbackCommand command) {
        // RN: Developer must exist
        var developer = findDeveloperByEmailUseCase.execute(command.developerEmail())
                .orElseThrow(() -> new AccountNotFoundException("Developer not found"));

        // RN: Developer must not be blocked
        if (developer.isBlocked())
            throw new AccountBlockedException("Developer is blocked");

        // RN: Exchange OAuth code for GitHub tokens and user info
        var gitHubAccount = gitHubApiPort.getAccount(command.code());

        // RN: Encrypt tokens before persisting
        var encryptedAccessToken = new EncryptedOAuthToken(encryptionPort.encrypt(gitHubAccount.accessToken()));
        var encryptedRefreshToken = new EncryptedOAuthToken(encryptionPort.encrypt(gitHubAccount.refreshToken()));

        var connection = OAuthConnection.createGitHub(
                developer.id(),
                new ProviderUserId(gitHubAccount.userId()),
                new ProviderUsername(gitHubAccount.username()),
                encryptedAccessToken,
                encryptedRefreshToken,
                new OAuthTokenExpiration(gitHubAccount.accessTokenExpiresAt()),
                new OAuthTokenExpiration(gitHubAccount.refreshTokenExpiresAt())
        );
        repositoryPort.save(connection);
    }

}
