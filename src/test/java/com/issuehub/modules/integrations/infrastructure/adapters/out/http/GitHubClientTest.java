package com.issuehub.modules.integrations.infrastructure.adapters.out.http;

import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.infrastructure.config.GitHubProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GitHubClientTest {

    private MockRestServiceServer mockServer;

    private GitHubClient gitHubClient;

    @BeforeEach
    void setup() {
        var restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        var properties = new GitHubProperties("test-client-id", "test-client-secret", "http://localhost/callback");
        gitHubClient = new GitHubClient(restClientBuilder.build(), properties);
    }

    @Test
    void shouldReturnAccountDto_whenGetAccountSucceeds() {
        // Given
        mockServer.expect(requestTo("https://github.com/login/oauth/access_token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                            "access_token": "ghu_accesstoken",
                            "expires_in": 28800,
                            "refresh_token": "ghr_refreshtoken",
                            "refresh_token_expires_in": 15897600
                        }
                        """, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://api.github.com/user"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                            "id": 102187164,
                            "login": "test"
                        }
                        """, MediaType.APPLICATION_JSON));

        // When
        var response = gitHubClient.getAccount("github-code-123");

        // Then
        assertThat(response.userId()).isEqualTo("102187164");
        assertThat(response.username()).isEqualTo("test");
        assertThat(response.accessToken()).isEqualTo("ghu_accesstoken");
        assertThat(response.refreshToken()).isEqualTo("ghr_refreshtoken");
        assertThat(response.accessTokenExpiresAt()).isAfter(Instant.now());
        assertThat(response.refreshTokenExpiresAt()).isAfter(Instant.now());

        mockServer.verify();
    }

    @Test
    void shouldThrowGitHubApiException_whenTokenResponseIsEmpty() {
        // Given
        mockServer.expect(requestTo("https://github.com/login/oauth/access_token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // When/Then
        assertThatThrownBy(() -> gitHubClient.getAccount("invalid-code"))
                .isInstanceOf(GitHubApiException.class);
    }

    @Test
    void shouldThrowGitHubApiException_whenUserResponseIsEmpty() {
        // Given
        mockServer.expect(requestTo("https://github.com/login/oauth/access_token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                            "access_token": "ghu_accesstoken",
                            "expires_in": 28800,
                            "refresh_token": "ghr_refreshtoken",
                            "refresh_token_expires_in": 15897600
                        }
                        """, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://api.github.com/user"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // When/Then
        assertThatThrownBy(() -> gitHubClient.getAccount("github-code-123"))
                .isInstanceOf(GitHubApiException.class);
    }

}
