package com.issuehub.modules.developers.infrastructure.adapters.in.http.controller;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.developers.infrastructure.adapters.in.http.dto.CreateDeveloperRequest;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@IntegrationTest
class DeveloperControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DeveloperJpaRepository developerRepository;

    private static final String CLEAN_DB = "/db/clean/developers.sql";
    private static final String DATA_DB = "/db/data/developers.sql";

    // === creation ===
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
