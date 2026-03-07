package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.exceptions.DeveloperNotFoundException;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.enums.DeveloperStatus;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperProfile;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactivateDeveloperServiceTest {

    @Mock
    private DeveloperRepositoryPort repositoryPort;

    @InjectMocks
    private ReactivateDeveloperService reactivateDeveloperService;

    @Test
    void shouldReactivateDeveloper() {
        // Given
        var developer = new Developer(
                EntityId.generate(),
                new DeveloperEmail("dev@example.com"),
                true,
                DeveloperStatus.DELETED,
                DeveloperProfile.defaultProfile(),
                Instant.now(),
                Instant.now()
        );

        when(repositoryPort.findById(developer.getId())).thenReturn(Optional.of(developer));

        // When
        reactivateDeveloperService.execute(developer.getId());

        // Then
        assertThat(developer.isActive()).isTrue();
        verify(repositoryPort).save(developer);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperNotFound() {
        // Given
        var developerId = EntityId.generate();
        when(repositoryPort.findById(developerId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> reactivateDeveloperService.execute(developerId))
                .isInstanceOf(DeveloperNotFoundException.class);
    }

}
