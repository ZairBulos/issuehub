package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.dto.internal.CreateDeveloperCommand;
import com.issuehub.modules.developers.application.exceptions.DeveloperAlreadyExistsException;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperProfile;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDeveloperServiceTest {

    @Mock
    private DeveloperRepositoryPort repositoryPort;

    @Mock
    private EventPublisherPort publisherPort;

    @InjectMocks
    private CreateDeveloperService createDeveloperService;

    @Test
    void shouldSaveDeveloperAndPublishEvent() {
        // Given
        var email = new DeveloperEmail("new@example.com");
        var command = new CreateDeveloperCommand(email);

        when(repositoryPort.findByEmail(email)).thenReturn(Optional.empty());

        // When
        createDeveloperService.execute(command);

        // Then
        verify(repositoryPort, times(1)).save(any());
        verify(publisherPort, times(1)).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenDeveloperAlreadyExists() {
        // Given
        var email = new DeveloperEmail("exists@example.com");
        var command = new CreateDeveloperCommand(email);
        var existingDeveloper = Developer.create(email, DeveloperProfile.defaultProfile());

        when(repositoryPort.findByEmail(email)).thenReturn(Optional.of(existingDeveloper));

        // When/Then
        assertThrows(DeveloperAlreadyExistsException.class, () -> createDeveloperService.execute(command));
        verify(repositoryPort, never()).save(any());
        verify(publisherPort, never()).publish(any());
    }

}
