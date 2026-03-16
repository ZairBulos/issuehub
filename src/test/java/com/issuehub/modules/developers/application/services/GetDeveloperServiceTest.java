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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDeveloperServiceTest {

    @Mock
    private DeveloperRepositoryPort repositoryPort;

    @InjectMocks
    private GetDeveloperService getDeveloperService;

    @Test
    void shouldReturnDeveloperDTO() {
        // Given
        var developer = new Developer(
                EntityId.generate(),
                new DeveloperEmail("dev@example.com"),
                true,
                DeveloperStatus.ACTIVE,
                DeveloperProfile.defaultProfile(),
                Instant.now(),
                Instant.now()
        );
        var developerEmail = developer.getEmail();

        when(repositoryPort.findByEmail(developerEmail)).thenReturn(Optional.of(developer));

        // When
        var result = getDeveloperService.execute(developerEmail);

        // Then
        assertThat(result.id()).isEqualTo(developer.getId().value());
        assertThat(result.email()).isEqualTo(developer.getEmail().value());
        assertThat(result.name()).isEqualTo(developer.getProfile().name());
        assertThat(result.language()).isEqualTo(developer.getProfile().language());
        assertThat(result.timezone()).isEqualTo(developer.getProfile().timezone());
    }

    @Test
    void shouldThrowExceptionWhenDeveloperNotFound() {
        // Given
        var developerEmail = new DeveloperEmail("not_found@example.com");

        when(repositoryPort.findByEmail(developerEmail)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> getDeveloperService.execute(developerEmail))
                .isInstanceOf(DeveloperNotFoundException.class);
    }

}
