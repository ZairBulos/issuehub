package com.issuehub.modules.integrations.infrastructure.adapters.out.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.issuehub.modules.integrations.application.dto.GitHubOAuthResponse;
import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.infrastructure.config.GitHubProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GitHubClient implements GitHubApiPort {

    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";

    private final RestClient restClient;
    private final GitHubProperties gitHubProperties;

    @Override
    public GitHubOAuthResponse exchangeCode(String code) {
        var tokenResponse = fetchAccessToken(code);
        var userResponse = fetchUser(tokenResponse.accessToken());

        return new GitHubOAuthResponse(
                String.valueOf(userResponse.id()),
                userResponse.login(),
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                Instant.now().plusSeconds(tokenResponse.expiresIn()),
                Instant.now().plusSeconds(tokenResponse.refreshTokenExpiresIn())
        );
    }

    private GitHubTokenResponse fetchAccessToken(String code) {
        var response = restClient.post()
                .uri(GITHUB_TOKEN_URL)
                .header("Accept", "application/json")
                .body(Map.of(
                        "client_id", gitHubProperties.clientId(),
                        "client_secret", gitHubProperties.clientSecret(),
                        "code", code
                ))
                .retrieve()
                .body(GitHubTokenResponse.class);

        if (response == null || response.accessToken() == null)
            throw new GitHubApiException("Failed to obtain access token from GitHub");

        return response;
    }

    private GitHubUserResponse fetchUser(String accessToken) {
        var response = restClient.get()
                .uri(GITHUB_USER_URL)
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GitHubUserResponse.class);

        if (response == null)
            throw new GitHubApiException("Failed to obtain user info from GitHub");

        return response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") long expiresIn,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("refresh_token_expires_in") long refreshTokenExpiresIn
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubUserResponse(
            @JsonProperty("id") long id,
            @JsonProperty("login") String login
    ) {}

}
