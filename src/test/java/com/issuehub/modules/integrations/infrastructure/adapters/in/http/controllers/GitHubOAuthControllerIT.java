package com.issuehub.modules.integrations.infrastructure.adapters.in.http.controllers;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.integrations.application.dto.GitHubAccountDto;
import com.issuehub.modules.integrations.application.dto.GitHubRepositoryDto;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.repositories.OAuthConnectionJpaRepository;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@IntegrationTest
class GitHubOAuthControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenProviderPort tokenProviderPort;

    @MockitoBean
    private GitHubApiPort gitHubApiPort;

    @Autowired
    private OAuthConnectionJpaRepository oAuthConnectionRepository;

    private static final String CLEAN_DB = "/db/clean/oauth_connections.sql";
    private static final String DATA_DB = "/db/data/oauth_connections.sql";

    // === callback ===
    @Nested
    class Callback {

        private static final String CONNECT  = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.CONNECT;
        private static final String CALLBACK = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.CALLBACK;

        private HttpEntity<Void> authenticated() {
            var token = tokenProviderPort.generateAccessToken(
                    "callback@example.com",
                    Map.of("developerId", "6b6cd56c-d3d4-447b-86b1-8c287e45031b")
            );

            var headers = new HttpHeaders();
            headers.setBearerAuth(token);
            return new HttpEntity<>(headers);
        }

        private GitHubAccountDto githubAccount() {
            return new GitHubAccountDto(
                    "12345678",
                    "it_test",
                    "ghu_accesstoken",
                    "ghr_refreshtoken",
                    Instant.now().plusSeconds(28800),
                    Instant.now().plusSeconds(15897600)
            );
        }

        @Test
        @Sql({CLEAN_DB, DATA_DB})
        void callback_shouldReturn200AndPersistConnection_whenSucceeds() {
            // Given
            when(gitHubApiPort.getAccount(any())).thenReturn(githubAccount());

            var connectResponse = restTemplate.exchange(
                    CONNECT,
                    HttpMethod.GET,
                    authenticated(),
                    Void.class
            );
            var state = Arrays.stream(connectResponse.getHeaders().getLocation().toString().split("&"))
                    .filter(p -> p.startsWith("state="))
                    .findFirst()
                    .map(p -> p.replace("state=", ""))
                    .orElseThrow();

            // When
            var response = restTemplate.getForEntity(
                    CALLBACK + "?code=github-code-123&state=" + state,
                    Void.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            var connections = oAuthConnectionRepository.findAll();
            var connection = connections.stream()
                    .filter(c ->  c.getProviderUserId().equals("12345678"))
                    .findFirst()
                    .orElseThrow();

            assertThat(connection.getProviderUserId()).isEqualTo("12345678");
            assertThat(connection.getProviderUsername()).isEqualTo("it_test");
            assertThat(connection.getEncryptedAccessToken()).isNotBlank();
            assertThat(connection.getEncryptedRefreshToken()).isNotBlank();
        }

    }

    // === repositories ===
    @Nested
    @Sql({CLEAN_DB, DATA_DB})
    class GetRepositories {

        private static final String REPOSITORIES = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.REPOSITORIES;

        private HttpEntity<Void> authenticated() {
            var token = tokenProviderPort.generateAccessToken(
                    "connections@example.com",
                    Map.of("developerId", "d12333cc-1292-47b2-abe7-193478f93e17")
            );

            var headers = new HttpHeaders();
            headers.setBearerAuth(token);
            return new HttpEntity<>(headers);
        }

        @Test
        @Sql({CLEAN_DB, DATA_DB})
        void repositories_shouldReturn200_whenConnectionExists() {
            // Given
            when(gitHubApiPort.getRepositories(any(), any(), anyInt(), anyInt()))
                    .thenReturn(List.of(new GitHubRepositoryDto(1L, "my-repo", "it_test/my-repo", "it_test")));

            // When
            var response = restTemplate.exchange(
                    REPOSITORIES + "?providerUserId=987654321",
                    HttpMethod.GET,
                    authenticated(),
                    Void.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

    }

}
