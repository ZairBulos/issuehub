package com.issuehub.modules.integrations.application.services;

import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.domain.models.valueobjects.EncryptedOAuthToken;
import com.issuehub.modules.integrations.domain.models.valueobjects.OAuthTokenExpiration;

import java.time.Instant;

public class GitHubRefreshTokenService {

    private final GitHubApiPort gitHubApiPort;
    private final OAuthConnectionRepositoryPort repositoryPort;
    private final EncryptionPort encryptionPort;

    public GitHubRefreshTokenService(
            GitHubApiPort gitHubApiPort,
            OAuthConnectionRepositoryPort repositoryPort,
            EncryptionPort encryptionPort
    ) {
        this.gitHubApiPort = gitHubApiPort;
        this.repositoryPort = repositoryPort;
        this.encryptionPort = encryptionPort;
    }

    public void execute(OAuthConnection connection) {
        if (!connection.isAccessTokenExpired(Instant.now())) return;

        var refreshToken = encryptionPort.decrypt(connection.getEncryptedRefreshToken().value());

        var gitHubRefreshedToken = gitHubApiPort.refreshToken(refreshToken);

        connection.refreshTokens(
                new EncryptedOAuthToken(encryptionPort.encrypt(gitHubRefreshedToken.accessToken())),
                new EncryptedOAuthToken(encryptionPort.encrypt(gitHubRefreshedToken.refreshToken())),
                new OAuthTokenExpiration(gitHubRefreshedToken.accessTokenExpiresAt()),
                new OAuthTokenExpiration(gitHubRefreshedToken.refreshTokenExpiresAt())
        );
        repositoryPort.save(connection);
    }

}
