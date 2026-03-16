package com.issuehub.modules.developers.infrastructure.adapters.in.http.controller;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.developers.application.dto.internal.DeveloperDTO;
import com.issuehub.modules.developers.infrastructure.adapters.in.http.dto.CreateDeveloperRequest;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@IntegrationTest
class DeveloperControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DeveloperJpaRepository developerRepository;

    private static final String CLEAN_DB = "/db/clean/developers.sql";
    private static final String DATA_DB = "/db/data/developers.sql";
    @Autowired
    private TokenProviderPort tokenProviderPort;

    // === creation ===
    @Nested
    class Create {

        @Test
        @Sql(CLEAN_DB)
        void createDeveloper_shouldReturn201Created_whenRequestIsValid() {
            // Given
            var email = "it@example.com";
            var request = new CreateDeveloperRequest(email);

            // When
            var response = restTemplate.postForEntity(
                    DeveloperController.DEVELOPERS,
                    request,
                    Void.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getHeaders().getLocation()).isNotNull();

            assertThat(developerRepository.findByEmail(email)).isPresent();
        }

        @Test
        @Sql({CLEAN_DB, DATA_DB})
        void createDeveloper_shouldReturn409Conflict_whenEmailAlreadyExists() {
            // Given
            var request = new CreateDeveloperRequest("dummy@example.com");

            // When
            var response = restTemplate.postForEntity(
                    DeveloperController.DEVELOPERS,
                    request,
                    Void.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

    }

    // === me ===
    @Nested
    class GetMe {

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
        @Sql({CLEAN_DB, DATA_DB})
        void me_shouldReturn200Ok_withDeveloperInfo() {
            // When
            var response = restTemplate.exchange(
                    DeveloperController.DEVELOPERS + DeveloperController.ME,
                    HttpMethod.GET,
                    authenticated(),
                    DeveloperDTO.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().email()).isEqualTo("dummy@example.com");
            assertThat(response.getBody().name()).isEqualTo("Dummy");
            assertThat(response.getBody().language()).isEqualTo("EN");
            assertThat(response.getBody().timezone()).isEqualTo("UTC");
        }

    }

}
