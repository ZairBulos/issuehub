package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.dto.internal.VerifyEmailCommand;
import com.issuehub.modules.auth.application.ports.out.EmailVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.issuehub.modules.auth.domain.models.aggregates.EmailVerification;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationExpiration;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.BiPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceTest {

    @Mock
    private EmailVerificationRepositoryPort repositoryPort;

    @Mock
    private EventPublisherPort publisherPort;

    private final BiPredicate<String, String> verifier = String::equals;

    private VerifyEmailService verifyEmailService;

    private EntityId developerId;
    private VerificationCode code;

    @BeforeEach
    void setup() {
        verifyEmailService = new VerifyEmailService(repositoryPort, publisherPort, verifier);
        developerId = EntityId.generate();
        code = VerificationCode.generate();
    }

    @Test
    void shouldMarkVerificationAsUsedAndPublishEvent() {
        // Given
        var emailVerification =
                EmailVerification.create(developerId, code.toHashed(c -> c), VerificationExpiration.generate());
        var command = new VerifyEmailCommand(developerId, code);

        when(repositoryPort.findByDeveloperId(developerId)).thenReturn(Optional.of(emailVerification));

        // When
        verifyEmailService.execute(command);

        // Then
        assertThat(emailVerification.isUsed()).isTrue();
        verify(repositoryPort, times(1)).save(emailVerification);
        verify(publisherPort, times(1)).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenCodeNotFound() {
        // Given
        var command = new VerifyEmailCommand(developerId, code);

        when(repositoryPort.findByDeveloperId(developerId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> verifyEmailService.execute(command))
                .isInstanceOf(InvalidVerificationCodeException.class);
        verify(repositoryPort, never()).save(any());
        verify(publisherPort, never()).publish(any());
    }

}
