package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.exceptions.AccountBlockedException;
import com.issuehub.modules.auth.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.auth.application.exceptions.AccountNotVerifiedException;
import com.issuehub.modules.auth.application.ports.out.LoginVerificationRepositoryPort;
import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoginServiceTest {

    @Mock
    private LoginVerificationRepositoryPort repositoryPort;

    @Mock
    private FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;

    @Mock
    private EventPublisherPort publisherPort;

    private final Function<String, String> hasher = code -> "hashed_" + code;

    private RequestLoginService requestLoginService;

    @BeforeEach
    void setup() {
        requestLoginService = new RequestLoginService(repositoryPort, findDeveloperByEmailUseCase, publisherPort, hasher);
    }

    @Test
    void shouldSaveLoginVerificationAndPublishEvent() {
        // Given
        var email = "test@example.com";
        var developer = new DeveloperView(EntityId.generate(), email, true, "active");

        when(findDeveloperByEmailUseCase.execute(email)).thenReturn(Optional.of(developer));

        // When
        requestLoginService.execute(email);

        // Then
        verify(repositoryPort, times(1)).replaceActiveVerification(any());
        verify(publisherPort, times(1)).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenDeveloperNotFound() {
        // Given
        var email = "missing@example.com";

        when(findDeveloperByEmailUseCase.execute(email)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> requestLoginService.execute(email))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperIsBlocked() {
        // Given
        var email = "blocked@example.com";
        var blockedDeveloper = new DeveloperView(EntityId.generate(), email, true, "blocked");

        when(findDeveloperByEmailUseCase.execute(email)).thenReturn(Optional.of(blockedDeveloper));

        // When/Then
        assertThatThrownBy(() -> requestLoginService.execute(email))
                .isInstanceOf(AccountBlockedException.class);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperIsNotVerified() {
        // Given
        var email = "not_verified@example.com";
        var notVerifiedDeveloper = new DeveloperView(EntityId.generate(), email, false, "active");

        when(findDeveloperByEmailUseCase.execute(email)).thenReturn(Optional.of(notVerifiedDeveloper));

        // When/Then
        assertThatThrownBy(() -> requestLoginService.execute(email))
                .isInstanceOf(AccountNotVerifiedException.class);
    }

}
