package com.issuehub.modules.developers.infrastructure.adapters.in.messaging;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import com.issuehub.shared.domain.events.EmailVerified;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@ApplicationModuleTest
public class EmailVerifiedListenerIntegrationTest {

    @Autowired
    private DeveloperJpaRepository developerRepository;

    private static final String CLEAN_DB = "/db/clean/developers.sql";
    private static final String DATA_DB = "/db/data/developers.sql";

    @Test
    @Sql({CLEAN_DB, DATA_DB})
    void whenEmailVerifiedEventPublished_thenDeveloperIsVerified(Scenario scenario) {
        // Given
        var event = new EmailVerified(
                new EntityId(UUID.fromString("00000000-0000-0000-0000-000000000000")),
                Instant.now()
        );

        // When/Then
        scenario.publish(event)
                .andWaitForEventOfType(EmailVerified.class)
                .toArriveAndVerify(emailVerified -> {
                    assertThat(emailVerified.developerId()).isEqualTo(event.developerId());
                });

        var developer = developerRepository.findById(event.developerId().value()).orElseThrow();
        assertThat(developer.getIsVerified()).isTrue();
    }

}
