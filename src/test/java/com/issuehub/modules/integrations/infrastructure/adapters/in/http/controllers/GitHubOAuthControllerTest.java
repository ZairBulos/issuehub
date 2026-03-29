package com.issuehub.modules.integrations.infrastructure.adapters.in.http.controllers;

import com.issuehub.modules.integrations.application.dto.GitHubRepositoryDto;
import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.application.exceptions.OAuthConnectionNotFoundException;
import com.issuehub.modules.integrations.application.ports.in.GitHubCallbackUseCase;
import com.issuehub.modules.integrations.application.ports.in.ListGitHubRepositoriesUseCase;
import com.issuehub.modules.integrations.infrastructure.config.GitHubProperties;
import com.issuehub.shared.application.dto.DecodedToken;
import com.issuehub.shared.application.exceptions.InvalidTokenException;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import com.issuehub.shared.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(GitHubOAuthController.class)
class GitHubOAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProviderPort tokenProviderPort;

    @MockitoBean
    private GitHubProperties gitHubProperties;

    @MockitoBean
    private GitHubCallbackUseCase gitHubCallbackUseCase;

    @MockitoBean
    private ListGitHubRepositoriesUseCase listGitHubRepositoriesUseCase;

    // === connect ===
    @Nested
    class Connect {

        private static final String CONNECT = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.CONNECT;

        private MockHttpServletRequestBuilder authenticated() {
            return get(CONNECT)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        }

        @BeforeEach
        void setup() {
            when(gitHubProperties.clientId()).thenReturn("test-client-id");
            when(gitHubProperties.clientSecret()).thenReturn("http://localhost:8080/integrations/github/callback");

            when(tokenProviderPort.verifyAccessToken("valid-token"))
                    .thenReturn(new DecodedToken(
                            UUID.randomUUID().toString(),
                            "dev@example.com",
                            Map.of(),
                            Instant.now(),
                            Instant.now().plusSeconds(900)
                    ));
        }

        @Test
        void connect_shouldReturn200WithLocationHeader_whenAuthenticated() throws Exception {
            // When/Then
            mockMvc.perform(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(header().string("Location", containsString("https://github.com/login/oauth/authorize")))
                    .andExpect(header().string("Location", containsString("client_id=test-client-id")))
                    .andExpect(header().string("Location", containsString("state=")));
        }

        @Test
        void connect_shouldReturn401Unauthorized_whenTokenIsMissing() throws Exception {
            // When/Then
            mockMvc.perform(get(CONNECT))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void connect_shouldReturn401Unauthorized_whenTokenIsInvalid() throws Exception {
            // Given
            when(tokenProviderPort.verifyAccessToken(any()))
                    .thenThrow(new InvalidTokenException("Invalid token"));

            // When/Then
            mockMvc.perform(get(CONNECT)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }

    }

    // === callback ===
    @Nested
    class Callback {

        private static final String CALLBACK = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.CALLBACK;

        @Test
        void callback_shouldReturn200Ok_whenSucceeds() throws Exception {
            // Given
            doNothing().when(gitHubCallbackUseCase).execute(any());

            // When/Then
            mockMvc.perform(get(CALLBACK)
                            .param("code", "github-code-123")
                            .param("state", "some-state"))
                    .andExpect(status().isOk());
        }

        @Test
        void callback_shouldReturn400BadRequest_whenCodeIsMissing() throws Exception {
            // When/Then
            mockMvc.perform(get(CALLBACK)
                            .param("state", "some-state"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void callback_shouldReturn400BadRequest_whenStateIsMissing() throws Exception {
            // When/Then
            mockMvc.perform(get(CALLBACK)
                            .param("code", "github-code-123"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void callback_shouldReturn502BadGateway_whenGitHubApiFails() throws Exception {
            // Given
            doThrow(new GitHubApiException("Failed to obtain access token from GitHub"))
                    .when(gitHubCallbackUseCase).execute(any());

            // When/Then
            mockMvc.perform(get(CALLBACK)
                            .param("code", "github-code-123")
                            .param("state", "some-state"))
                    .andExpect(status().isBadGateway());
        }

    }

    // === repositories ===
    @Nested
    class Repositories {

        private static final String REPOSITORIES = GitHubOAuthController.INTEGRATIONS_GITHUB + GitHubOAuthController.REPOSITORIES;

        private MockHttpServletRequestBuilder authenticated() {
            return get(REPOSITORIES)
                    .param("providerUserId", "12345678")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        }

        @BeforeEach
        void setup() {
            when(gitHubProperties.clientId()).thenReturn("test-client-id");
            when(gitHubProperties.clientSecret()).thenReturn("http://localhost:8080/integrations/github/callback");

            when(tokenProviderPort.verifyAccessToken("valid-token"))
                    .thenReturn(new DecodedToken(
                            UUID.randomUUID().toString(),
                            "dev@example.com",
                            Map.of(),
                            Instant.now(),
                            Instant.now().plusSeconds(900)
                    ));
        }

        @Test
        void repositories_shouldReturn200WithPagedResponse_whenAuthenticated() throws Exception {
            // Given
            when(listGitHubRepositoriesUseCase.execute(any())).thenReturn(List.of(
                    new GitHubRepositoryDto(1L, "repo", "octocat/repo", "octocat")
            ));

            // When/Then
            mockMvc.perform(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items[0].name").value("repo"))
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.pageSize").value(30))
                    .andExpect(jsonPath("$.totalItems").value(1));
        }

        @Test
        void repositories_shouldReturn401Unauthorized_whenTokenIsMissing() throws Exception {
            // When/Then
            mockMvc.perform(get(REPOSITORIES).param("providerUserId", "12345678"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void repositories_shouldReturn401Unauthorized_whenTokenIsInvalid() throws Exception {
            // Given
            when(tokenProviderPort.verifyAccessToken(any()))
                    .thenThrow(new InvalidTokenException("Invalid token"));

            // When/Then
            mockMvc.perform(get(REPOSITORIES)
                            .param("providerUserId", "12345678")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void repositories_shouldReturn400BadRequest_whenProviderUserIdIsMissing() throws Exception {
            // When/Then
            mockMvc.perform(get(REPOSITORIES)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void repositories_shouldReturn404NotFound_whenOAuthConnectionNotFound() throws Exception {
            // Given
            when(listGitHubRepositoriesUseCase.execute(any()))
                    .thenThrow(new OAuthConnectionNotFoundException("GitHub connection not found"));

            // When/Then
            mockMvc.perform(authenticated())
                    .andExpect(status().isNotFound());
        }

        @Test
        void repositories_shouldReturn502BadGateway_whenGitHubApiFails() throws Exception {
            // Given
            when(listGitHubRepositoriesUseCase.execute(any()))
                    .thenThrow(new GitHubApiException("Failed to obtain repositories from GitHub"));

            // When/Then
            mockMvc.perform(authenticated())
                    .andExpect(status().isBadGateway());
        }

    }

}
