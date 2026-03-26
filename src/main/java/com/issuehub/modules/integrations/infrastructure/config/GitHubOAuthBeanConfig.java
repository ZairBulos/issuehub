package com.issuehub.modules.integrations.infrastructure.config;

import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.modules.integrations.application.ports.in.GitHubCallbackUseCase;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.modules.integrations.application.services.GitHubCallbackService;
import com.issuehub.modules.integrations.application.services.GitHubRefreshTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GitHubOAuthBeanConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public GitHubCallbackUseCase gitHubCallbackUseCase(
            GitHubApiPort gitHubApiPort,
            OAuthConnectionRepositoryPort repositoryPort,
            FindDeveloperByEmailUseCase findDeveloperByEmailUseCase,
            EncryptionPort encryptionPort
    ) {
        return new GitHubCallbackService(gitHubApiPort, repositoryPort, findDeveloperByEmailUseCase, encryptionPort);
    }

    @Bean
    GitHubRefreshTokenService gitHubRefreshTokenService(
            GitHubApiPort gitHubApiPort,
            OAuthConnectionRepositoryPort repositoryPort,
            EncryptionPort encryptionPort
    ) {
        return new GitHubRefreshTokenService(gitHubApiPort, repositoryPort, encryptionPort);
    }

}
