package com.issuehub.modules.developers.application.services;

import com.issuehub.modules.developers.application.exceptions.DeveloperNotFoundException;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperProfile;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyDeveloperServiceTest {

    @Mock
    private DeveloperRepositoryPort repositoryPort;

    @InjectMocks
    private VerifyDeveloperService verifyDeveloperService;

    @Test
    void shouldVerifyDeveloper() {
        // Given
        var developer = Developer.create(new DeveloperEmail("verify@example.com"), DeveloperProfile.defaultProfile());
        var developerId = developer.getId();

        when(repositoryPort.findById(developerId)).thenReturn(Optional.of(developer));

        // When
        verifyDeveloperService.execute(developerId);

        // Then
        assertThat(developer.getVerified()).isTrue();
        verify(repositoryPort).save(developer);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperNotFound() {
        // Given
        var developerId = EntityId.generate();

        when(repositoryPort.findById(developerId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> verifyDeveloperService.execute(developerId))
                .isInstanceOf(DeveloperNotFoundException.class);
    }

}
