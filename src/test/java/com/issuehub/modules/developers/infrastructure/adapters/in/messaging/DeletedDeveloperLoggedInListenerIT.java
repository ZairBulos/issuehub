package com.issuehub.modules.developers.infrastructure.adapters.in.messaging;

import com.issuehub.ModuleIntegrationTest;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import com.issuehub.shared.domain.events.DeletedDeveloperLoggedIn;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ModuleIntegrationTest
class DeletedDeveloperLoggedInListenerIT {

    @Autowired
    private DeveloperJpaRepository developerRepository;

    private static final String CLEAN_DB = "/db/clean/developers.sql";
    private static final String DATA_DB = "/db/data/developers.sql";

    @Test
    @Sql({CLEAN_DB, DATA_DB})
    void whenDeletedDeveloperLoggedInEventPublished_thenDeveloperIsReactivated(Scenario scenario) {
        // Given
        var developerId = new EntityId(UUID.fromString("bc13cfa1-2a21-401e-abd5-ec324b984f4c"));
        var event = new DeletedDeveloperLoggedIn(developerId, Instant.now());

        // When/Then
        scenario.publish(event)
                .andWaitForEventOfType(DeletedDeveloperLoggedIn.class)
                .toArriveAndVerify( deletedDeveloperLoggedIn-> {
                    assertThat(deletedDeveloperLoggedIn.developerId()).isEqualTo(event.developerId());
                });

        var developer = developerRepository.findById(event.developerId().value()).orElseThrow();
        assertThat(developer.getStatus()).isEqualTo("active");
    }

}
