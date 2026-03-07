package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindDeveloperServiceTest {

    @Mock
    private DeveloperRepositoryPort repositoryPort;

    @InjectMocks
    private FindDeveloperService developerService;

    // === by id ===
    @Test
    void shouldReturnDeveloperViewById() {
        // Given
        var developerId = EntityId.generate();
        var view = new DeveloperView(developerId, "view@example.com", false, "active");

        when(repositoryPort.findViewById(developerId)).thenReturn(Optional.of(view));

        // When
        var result = developerService.execute(developerId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().isActive()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenDeveloperDoesNotExistById() {
        // Given
        var developerId = EntityId.generate();

        when(repositoryPort.findViewById(developerId)).thenReturn(Optional.empty());

        // When
        var result = developerService.execute(developerId);

        // Then
        assertThat(result).isEmpty();
    }

    // === by email ===
    @Test
    void shouldReturnDeveloperViewByEmail() {
        // Given
        var developerEmail = new DeveloperEmail("view@example.com");
        var view = new DeveloperView(EntityId.generate(), "view@example.com", false, "blocked");

        when(repositoryPort.findViewByEmail(developerEmail)).thenReturn(Optional.of(view));

        // When
        var result = developerService.execute(developerEmail.value());

        // Then
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().isBlocked()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenDeveloperDoesNotExistByEmail() {
        // Given
        var developerEmail = new DeveloperEmail("no_exists@example.com");

        when(repositoryPort.findViewByEmail(developerEmail)).thenReturn(Optional.empty());

        // When
        var result = developerService.execute(developerEmail.value());

        // Then
        assertThat(result).isEmpty();
    }

}
