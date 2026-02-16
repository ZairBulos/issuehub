package com.issuehub.modules.developers.infrastructure.adapters.in.http.controller;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.developers.infrastructure.adapters.in.http.dto.CreateDeveloperRequest;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
class DeveloperControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DeveloperJpaRepository developerRepository;

    @BeforeEach
    void setup() {
        developerRepository.deleteAllInBatch();
    }

    // === creation ===
    @Test
    void createDeveloper_shouldReturn201Created() {
        var request = new CreateDeveloperRequest("it@example.com");

        var response = restTemplate.postForEntity(
                DeveloperController.DEVELOPERS,
                request,
                Void.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create(DeveloperController.DEVELOPERS + DeveloperController.ME), response.getHeaders().getLocation());
    }

    @Test
    void createDeveloper_shouldReturn409ConflictWhenEmailAlreadyExists() {
        var request = new CreateDeveloperRequest("exists@example.com");

        var firstResponse = restTemplate.postForEntity(
                DeveloperController.DEVELOPERS,
                request,
                Void.class
        );
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        var secondResponse = restTemplate.postForEntity(
                DeveloperController.DEVELOPERS,
                request,
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
    }

}
