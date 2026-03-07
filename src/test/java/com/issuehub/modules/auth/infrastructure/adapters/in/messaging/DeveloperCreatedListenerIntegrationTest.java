package com.issuehub.modules.auth.infrastructure.adapters.in.messaging;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.EmailVerificationJpaRepository;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.shared.domain.events.DeveloperCreated;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@ApplicationModuleTest
class DeveloperCreatedListenerIntegrationTest {

    @MockitoBean
    private FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;

    @Autowired
    private EmailVerificationJpaRepository emailVerificationRepository;

    private static final String CLEAN_DB = "/db/clean/developers.sql";
    private static final String DATA_DB = "/db/data/developers.sql";

    @Test
    @Sql({CLEAN_DB, DATA_DB})
    void whenDeveloperCreatedEventPublished_thenEmailVerificationIsCreated(Scenario scenario) {
        // Given
        var event = new DeveloperCreated(
                new EntityId(UUID.fromString("00000000-0000-0000-0000-000000000000")),
                "dummy@example.com",
                Instant.now()
        );

        // When/Then
        scenario.publish(event)
                .andWaitForEventOfType(DeveloperCreated.class)
                .toArriveAndVerify(developerCreated -> {
                    assertThat(developerCreated.developerId()).isEqualTo(event.developerId());
                    assertThat(developerCreated.developerEmail()).isEqualTo(event.developerEmail());
                });

        var emailVerification = emailVerificationRepository.findByDeveloperId(event.developerId().value()).orElseThrow();
        assertThat(emailVerification.getHashedCode()).isNotBlank();
        assertThat(emailVerification.getUsedAt()).isNull();
        assertThat(emailVerification.getExpiresAt()).isAfter(Instant.now());
    }

}
