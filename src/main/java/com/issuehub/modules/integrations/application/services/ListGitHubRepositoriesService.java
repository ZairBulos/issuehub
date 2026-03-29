package com.issuehub.modules.integrations.application.services;

import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.modules.integrations.application.dto.GitHubRepositoryDto;
import com.issuehub.modules.integrations.application.dto.ListGitHubRepositoriesQuery;
import com.issuehub.modules.integrations.application.exceptions.AccountBlockedException;
import com.issuehub.modules.integrations.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.integrations.application.exceptions.OAuthConnectionNotFoundException;
import com.issuehub.modules.integrations.application.ports.in.ListGitHubRepositoriesUseCase;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.modules.integrations.domain.models.enums.OAuthProvider;

import java.util.List;

public class ListGitHubRepositoriesService implements ListGitHubRepositoriesUseCase {

    private final GitHubApiPort gitHubApiPort;
    private final OAuthConnectionRepositoryPort repositoryPort;
    private final FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;
    private final GitHubRefreshTokenService gitHubRefreshTokenService;
    private final EncryptionPort encryptionPort;

    public ListGitHubRepositoriesService(
            GitHubApiPort gitHubApiPort,
            OAuthConnectionRepositoryPort repositoryPort,
            FindDeveloperByEmailUseCase findDeveloperByEmailUseCase,
            GitHubRefreshTokenService gitHubRefreshTokenService,
            EncryptionPort encryptionPort
    ) {
        this.gitHubApiPort = gitHubApiPort;
        this.repositoryPort = repositoryPort;
        this.findDeveloperByEmailUseCase = findDeveloperByEmailUseCase;
        this.gitHubRefreshTokenService = gitHubRefreshTokenService;
        this.encryptionPort = encryptionPort;
    }

    @Override
    public List<GitHubRepositoryDto> execute(ListGitHubRepositoriesQuery query) {
        // RN: Developer must exist
        var developer = findDeveloperByEmailUseCase.execute(query.developerEmail())
                .orElseThrow(() -> new AccountNotFoundException("Developer not found"));

        // RN: Developer must not be blocked
        if (developer.isBlocked())
            throw new AccountBlockedException("Developer is blocked");

        // RN: Connection must exist for the given account
        var connection = repositoryPort
                .findByDeveloperIdAndProviderAndProviderUserId(developer.id(), OAuthProvider.GITHUB.value(), query.providerUserId())
                .orElseThrow(() -> new OAuthConnectionNotFoundException("GitHub connection not found"));

        // RN: Access token must be valid
        gitHubRefreshTokenService.execute(connection);

        var accessToken = encryptionPort.decrypt(connection.getEncryptedAccessToken().value());

        return gitHubApiPort.getRepositories(
                accessToken,
                connection.getProviderUsername().value(),
                query.page(),
                query.pageSize()
        );
    }

}
