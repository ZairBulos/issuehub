package com.issuehub.modules.auth.infrastructure.adapters.in.messaging;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.auth.application.ports.in.internal.CreateEmailVerificationUseCase;
import com.issuehub.modules.developers.domain.events.DeveloperCreated;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@IntegrationTest
@ApplicationModuleTest
class DeveloperCreatedListenerIntegrationTest {

    @MockitoBean
    private CreateEmailVerificationUseCase createEmailVerificationUseCase;

    @Test
    void whenDeveloperCreatedEventPublished_thenUseCaseIsInvoked(Scenario scenario) {
        var event = new DeveloperCreated(
                EntityId.generate(),
                "test@example.com",
                Instant.now()
        );

        scenario.publish(event)
                .andWaitForEventOfType(DeveloperCreated.class)
                .toArriveAndVerify(developerCreated -> {
                    verify(createEmailVerificationUseCase, timeout(1000)).execute(developerCreated);
                });
    }

}
