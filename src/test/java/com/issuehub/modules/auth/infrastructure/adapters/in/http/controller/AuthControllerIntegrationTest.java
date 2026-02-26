package com.issuehub.modules.auth.infrastructure.adapters.in.http.controller;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.auth.infrastructure.adapters.in.http.dto.RequestLoginRequest;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.EmailVerificationJpaRepository;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.LoginVerificationJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@IntegrationTest
public class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmailVerificationJpaRepository emailVerificationRepository;

    @Autowired
    private LoginVerificationJpaRepository loginVerificationRepository;

    private static final String CLEAN_DB_EMAIL_VERIFICATIONS = "/db/clean/email_verifications.sql";
    private static final String DATA_DB_EMAIL_VERIFICATIONS = "/db/data/email_verifications.sql";

    private static final String CLEAN_DB_LOGIN_VERIFICATIONS = "/db/clean/login_verifications.sql";
    private static final String DATA_DB_LOGIN_VERIFICATIONS = "/db/data/login_verifications.sql";

    private final String VERIFY_EMAIL = AuthController.AUTH + AuthController.VERIFY_EMAIL;
    private final String REQUEST_LOGIN = AuthController.AUTH + AuthController.REQUEST_LOGIN;

    // === verify email ===
    @Test
    @Sql({CLEAN_DB_EMAIL_VERIFICATIONS, DATA_DB_EMAIL_VERIFICATIONS})
    void verifyEmail_shouldReturn204NoContent_whenVerificationSucceeds() {
        // Given
        var developerId = "11111111-1111-1111-1111-111111111111";
        var code = "VALID_CODE_123";

        // When
        var response = restTemplate.getForEntity(
                VERIFY_EMAIL + "?developerId=" + developerId + "&code=" + code,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(emailVerificationRepository.findByDeveloperId(UUID.fromString(developerId)))
                .isPresent()
                .hasValueSatisfying(v -> assertThat(v.getUsedAt()).isNotNull());
    }

    // === request login ===
    @Test
    @Sql({CLEAN_DB_LOGIN_VERIFICATIONS, DATA_DB_LOGIN_VERIFICATIONS})
    void requestLogin_shouldReturn200Ok_whenLoginRequestSucceeds() {
        // Given
        var developerEmail = "request_login@example.com";
        var request = new RequestLoginRequest(developerEmail);

        // When
        var response = restTemplate.postForEntity(
                REQUEST_LOGIN,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var loginVerifications = loginVerificationRepository.findAll();
        assertThat(loginVerifications.size()).isEqualTo(2);

        var invalidated = loginVerifications.stream()
                .filter(v -> v.getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000")))
                .findFirst()
                .orElseThrow();
        assertThat(invalidated.getUsedAt()).isNotNull();

        var validated = loginVerifications.stream()
                .filter(v -> !v.getId().equals(invalidated.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(validated.getUsedAt()).isNull();
        assertThat(validated.getExpiresAt()).isAfter(Instant.now());
    }

}
