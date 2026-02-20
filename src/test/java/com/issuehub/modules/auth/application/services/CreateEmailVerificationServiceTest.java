package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.ports.out.EmailVerificationRepositoryPort;
import com.issuehub.shared.domain.events.DeveloperCreated;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateEmailVerificationServiceTest {

    @Mock
    private EmailVerificationRepositoryPort repositoryPort;

    @Mock
    private EventPublisherPort publisherPort;

    private final Function<String, String> hasher = code -> "hashed_" + code;

    private CreateEmailVerificationService createEmailVerificationService;

    @BeforeEach
    void setup() {
        createEmailVerificationService = new CreateEmailVerificationService(repositoryPort, publisherPort, hasher);
    }

    @Test
    void shouldSaveEmailVerificationAndPublishEvent() {
        // Given
        var developerId = EntityId.generate();
        var developerEmail = "test@example.com";
        var event = new DeveloperCreated(developerId, developerEmail, Instant.now());

        // When
        createEmailVerificationService.execute(event);

        // Then
        verify(repositoryPort, times(1)).save(any());
        verify(publisherPort, times(1)).publish(any());
    }

}
