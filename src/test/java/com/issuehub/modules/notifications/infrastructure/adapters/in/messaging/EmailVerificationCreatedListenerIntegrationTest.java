package com.issuehub.modules.notifications.infrastructure.adapters.in.messaging;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.auth.domain.events.EmailVerificationCreated;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@ApplicationModuleTest
class EmailVerificationCreatedListenerIntegrationTest {

    @Test
    void whenEmailVerificationCreatedEventPublished_thenListenerLogsEvent(Scenario scenario) {
        // Given
        var event = new EmailVerificationCreated(
                EntityId.generate(),
                EntityId.generate(),
                "test@example.com",
                "verification-code-123",
                Instant.now()
        );

        // When/Then
        scenario.publish(event)
                .andWaitForEventOfType(EmailVerificationCreated.class)
                .toArriveAndVerify(emailVerificationCreated -> {
                    assertThat(emailVerificationCreated.developerEmail()).isEqualTo("test@example.com");
                    assertThat(emailVerificationCreated.verificationCode()).isEqualTo("verification-code-123");
                });
    }

}
