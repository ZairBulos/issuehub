package com.issuehub.modules.notifications.infrastructure.adapters.in.messaging;

import com.issuehub.IntegrationTest;
import com.issuehub.modules.auth.domain.events.LoginVerificationCreated;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@ApplicationModuleTest
class LoginVerificationCreatedListenerIntegrationTest {

    @Test
    void whenLoginVerificationCreatedEventPublished_thenListenerLogsEvent(Scenario scenario) {
        // Given
        var event = new LoginVerificationCreated(
                EntityId.generate(),
                EntityId.generate(),
                "test@example.com",
                "123456",
                Instant.now()
        );

        // When/Then
        scenario.publish(event)
                .andWaitForEventOfType(LoginVerificationCreated.class)
                .toArriveAndVerify(loginVerificationCreated -> {
                    assertThat(loginVerificationCreated.developerEmail()).isEqualTo("test@example.com");
                    assertThat(loginVerificationCreated.verificationCode()).isEqualTo("123456");
                });
    }

}
