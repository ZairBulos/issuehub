package com.issuehub.modules.integrations.infrastructure.adapters.in.http.controllers;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.integrations.application.dto.GitHubOAuthResponse;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private static final String DATA_DB_DEVELOPERS = "/db/data/developers.sql";

    // === callback ===
    @Nested
    class Callback {

        private static final String CONNECT  = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.CONNECT;
        private static final String CALLBACK = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.CALLBACK;

        private GitHubOAuthResponse gitHubOAuthResponse() {
            return new GitHubOAuthResponse(
                    "12345678",
                    "it_test",
                    "ghu_accesstoken",
                    "ghr_refreshtoken",
                    Instant.now().plusSeconds(28800),
                    Instant.now().plusSeconds(15897600)
            );
        }

        private HttpEntity<Void> authenticated() {
            var token = tokenProviderPort.generateAccessToken(
                    "dummy@example.com",
                    Map.of("developerId", "00000000-0000-0000-0000-000000000000")
            );

            var headers = new HttpHeaders();
            headers.setBearerAuth(token);
            return new HttpEntity<>(headers);
        }

        @Test
        @Sql({CLEAN_DB, DATA_DB_DEVELOPERS})
        void callback_shouldReturn200AndPersistConnection_whenSucceeds() {
            // Given
            when(gitHubApiPort.exchangeCode(any())).thenReturn(gitHubOAuthResponse());

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
            assertThat(connections).hasSize(1);
            assertThat(connections.get(0).getProviderUserId()).isEqualTo("12345678");
            assertThat(connections.get(0).getProviderUsername()).isEqualTo("it_test");
            assertThat(connections.get(0).getEncryptedAccessToken()).isNotBlank();
            assertThat(connections.get(0).getEncryptedRefreshToken()).isNotBlank();
        }

    }

}
