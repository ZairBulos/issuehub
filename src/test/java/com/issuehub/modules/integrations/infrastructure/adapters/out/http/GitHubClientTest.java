package com.issuehub.modules.integrations.infrastructure.adapters.out.http;

import com.issuehub.modules.integrations.application.exceptions.GitHubApiException;
import com.issuehub.modules.integrations.infrastructure.config.GitHubProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
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

    @Nested
    class GetAccount {

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

    @Nested
    class RefreshToken {

        @Test
        void shouldReturnRefreshedTokenDto_whenRefreshTokenSucceeds() {
            // Given
            mockServer.expect(requestTo("https://github.com/login/oauth/access_token"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withSuccess("""
                        {
                            "access_token": "ghu_new_accesstoken",
                            "expires_in": 28800,
                            "refresh_token": "ghr_new_refreshtoken",
                            "refresh_token_expires_in": 15897600
                        }
                        """, MediaType.APPLICATION_JSON));

            // When
            var response = gitHubClient.refreshToken("ghr_refreshtoken");

            // Then
            assertThat(response.accessToken()).isEqualTo("ghu_new_accesstoken");
            assertThat(response.refreshToken()).isEqualTo("ghr_new_refreshtoken");
            assertThat(response.accessTokenExpiresAt()).isAfter(Instant.now());
            assertThat(response.refreshTokenExpiresAt()).isAfter(Instant.now());

            mockServer.verify();
        }

        @Test
        void shouldThrowGitHubApiException_whenRefreshTokenResponseIsEmpty() {
            // Given
            mockServer.expect(requestTo("https://github.com/login/oauth/access_token"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

            // When/Then
            assertThatThrownBy(() -> gitHubClient.refreshToken("ghr_expired_token"))
                    .isInstanceOf(GitHubApiException.class);
        }

    }

    @Nested
    class GetRepositories {

        @Test
        void shouldReturnRepositories_whenGetRepositoriesSucceeds() {
            // Given
            mockServer.expect(requestTo(containsString("https://api.github.com/users/octocat/repos")))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess("""
                        [
                            {
                                "id": 1,
                                "name": "repo",
                                "full_name": "octocat/repo",
                                "owner": { "login": "octocat" },
                                "has_issues": true,
                                "archived": false,
                                "disabled": false,
                                "permissions": { "admin": false, "push": true, "pull": true }
                            }
                        ]
                        """, MediaType.APPLICATION_JSON));

            // When
            var response = gitHubClient.getRepositories("ghu_accesstoken", "octocat", 1, 30);

            // Then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).name()).isEqualTo("repo");
            assertThat(response.get(0).fullName()).isEqualTo("octocat/repo");
            assertThat(response.get(0).ownerName()).isEqualTo("octocat");

            mockServer.verify();
        }

        @Test
        void shouldReturnEmptyList_whenNoRepositoriesMatchFilter() {
            // Given
            mockServer.expect(requestTo(containsString("https://api.github.com/users/octocat/repos")))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess("""
                        [
                            {
                                "id": 1,
                                "name": "read-only-repo",
                                "full_name": "octocat/read-only-repo",
                                "owner": { "login": "octocat" },
                                "has_issues": true,
                                "archived": false,
                                "disabled": false,
                                "permissions": { "admin": false, "push": false, "pull": true }
                            }
                        ]
                        """, MediaType.APPLICATION_JSON));

            // When
            var response = gitHubClient.getRepositories("ghu_accesstoken", "octocat", 1, 30);

            // Then
            assertThat(response).isEmpty();

            mockServer.verify();
        }

        @Test
        void shouldThrowGitHubApiException_whenRepositoriesResponseIsNull() {
            // Given
            mockServer.expect(requestTo(containsString("https://api.github.com/users/octocat/repos")))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

            // When/Then
            assertThatThrownBy(() -> gitHubClient.getRepositories("ghu_accesstoken", "octocat", 1, 30))
                    .isInstanceOf(GitHubApiException.class);
        }

    }

}
