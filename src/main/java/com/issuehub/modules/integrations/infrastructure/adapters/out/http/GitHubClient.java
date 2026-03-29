package com.issuehub.modules.integrations.infrastructure.adapters.out.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.issuehub.modules.integrations.application.dto.GitHubAccountDto;
import com.issuehub.modules.integrations.application.dto.GitHubRefreshedTokenDto;
import com.issuehub.modules.integrations.application.dto.GitHubRepositoryDto;
import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.application.ports.out.GitHubApiPort;
import com.issuehub.modules.integrations.infrastructure.config.GitHubProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GitHubClient implements GitHubApiPort {

    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";
    private static final String GITHUB_REPOS_URL = "https://api.github.com/users/{username}/repos";

    private final RestClient restClient;
    private final GitHubProperties gitHubProperties;

    @Override
    public GitHubAccountDto getAccount(String code) {
        var tokenResponse = fetchAccessToken(code);
        var userResponse = fetchUser(tokenResponse.accessToken());

        return new GitHubAccountDto(
                String.valueOf(userResponse.id()),
                userResponse.login(),
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                Instant.now().plusSeconds(tokenResponse.expiresIn()),
                Instant.now().plusSeconds(tokenResponse.refreshTokenExpiresIn())
        );
    }

    @Override
    public GitHubRefreshedTokenDto refreshToken(String refreshToken) {
        var tokenResponse = fetchRefreshToken(refreshToken);

        return new GitHubRefreshedTokenDto(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                Instant.now().plusSeconds(tokenResponse.expiresIn()),
                Instant.now().plusSeconds(tokenResponse.refreshTokenExpiresIn())
        );
    }

    @Override
    public List<GitHubRepositoryDto> getRepositories(String accessToken, String username, int page, int pageSize) {
        var repositoriesResponse = fetchRepositories(accessToken, username, page, pageSize);

        return repositoriesResponse.stream()
                .filter(r ->
                        r.hasIssues() && !r.archived() && !r.disabled() && r.permissions().push()
                )
                .map(r ->
                        new GitHubRepositoryDto(r.id(), r.name(), r.fullName(), r.owner().login())
                )
                .toList();
    }

    private GitHubTokenResponse fetchAccessToken(String code) {
        var response = restClient.post()
                .uri(GITHUB_TOKEN_URL)
                .header(HttpHeaders.ACCEPT, "application/json")
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

    private GitHubTokenResponse fetchRefreshToken(String refreshToken) {
        var response = restClient.post()
                .uri(GITHUB_TOKEN_URL)
                .header(HttpHeaders.ACCEPT, "application/json")
                .body(Map.of(
                        "client_id", gitHubProperties.clientId(),
                        "client_secret", gitHubProperties.clientSecret(),
                        "grant_type", "refresh_token",
                        "refresh_token", refreshToken
                ))
                .retrieve()
                .body(GitHubTokenResponse.class);

        if (response == null || response.accessToken() == null)
            throw new GitHubApiException("Failed to refresh token from GitHub");

        return response;
    }

    private GitHubUserResponse fetchUser(String accessToken) {
        var response = restClient.get()
                .uri(GITHUB_USER_URL)
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(GitHubUserResponse.class);

        if (response == null || response.login() == null)
            throw new GitHubApiException("Failed to obtain user info from GitHub");

        return response;
    }

    private List<GitHubRepositoryResponse> fetchRepositories(
            String accessToken,
            String username,
            int page,
            int perPage
    ) {
        var response = restClient.get()
                .uri(
                        GITHUB_REPOS_URL + "?page={page}&per_page={perPage}&sort=updated",
                        username, page, perPage
                )
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubRepositoryResponse>>() {});

        if (response == null)
            throw new GitHubApiException("Failed to obtain repositories from GitHub");

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubRepositoryResponse(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("full_name") String fullName,
            @JsonProperty("owner") GitHubOwnerResponse owner,
            @JsonProperty("has_issues") boolean hasIssues,
            @JsonProperty("archived") boolean archived,
            @JsonProperty("disabled") boolean disabled,
            @JsonProperty("permissions") GitHubPermissionsResponse permissions
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubOwnerResponse(
            @JsonProperty("login") String login
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubPermissionsResponse(
            @JsonProperty("push") boolean push
    ) {}

}
