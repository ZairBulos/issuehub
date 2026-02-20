package com.issuehub.modules.auth.infrastructure.adapters.in.http.controller;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.EmailVerificationJpaRepository;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@IntegrationTest
public class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmailVerificationJpaRepository emailVerificationRepository;

    private static final String CLEAN_DB = "/db/clean/email_verifications.sql";
    private static final String DATA_DB = "/db/data/email_verifications.sql";

    private final String VERIFY_EMAIL = AuthController.AUTH + AuthController.VERIFY_EMAIL;

    // === verify email ===
    @Test
    @Sql({CLEAN_DB, DATA_DB})
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

    @Test
    @Sql(CLEAN_DB)
    void verifyEmail_shouldReturn400BadRequest_whenVerificationNotFound() {
        // Given
        var developerId = EntityId.generate().value();
        var code = VerificationCode.generate().value();

        // When
        var response = restTemplate.getForEntity(
                VERIFY_EMAIL + "?developerId=" + developerId + "&code=" + code,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
