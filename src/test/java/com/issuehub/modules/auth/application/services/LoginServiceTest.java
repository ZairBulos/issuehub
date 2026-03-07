package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.dto.LoginCommand;
import com.issuehub.modules.auth.application.exceptions.AccountBlockedException;
import com.issuehub.modules.auth.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.auth.application.exceptions.ActiveLoginCodeNotFoundException;
import com.issuehub.modules.auth.application.ports.out.AuthSessionRepositoryPort;
import com.issuehub.modules.auth.application.ports.out.LoginVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.models.aggregates.AuthSession;
import com.issuehub.modules.auth.domain.models.aggregates.LoginVerification;
import com.issuehub.modules.auth.domain.models.valueobjects.IpAddress;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCodeExpiration;
import com.issuehub.modules.auth.domain.models.valueobjects.UserAgent;
import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import com.issuehub.shared.domain.events.DeletedDeveloperLoggedIn;
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
class LoginServiceTest {

    @Mock
    private LoginVerificationRepositoryPort repositoryPort;

    @Mock
    private AuthSessionRepositoryPort authSessionRepositoryPort;

    @Mock
    private FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private EventPublisherPort publisherPort;

    private final BiPredicate<String, String> verifier = String::equals;

    private LoginService loginService;

    // === fixtures ===
    private static final String EMAIL = "dev@example.com";
    private static final LoginCode CODE = new LoginCode("123456");
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    private LoginCommand command() {
        return new LoginCommand(EMAIL, CODE, new IpAddress("10.0.0.1"), new UserAgent("Mozilla/5.0"));
    }

    private DeveloperView activeDeveloper() {
        return new DeveloperView(EntityId.generate(), EMAIL, true, "active");
    }

    private DeveloperView blockedDeveloper() {
        return new DeveloperView(EntityId.generate(), EMAIL, true, "blocked");
    }

    private DeveloperView deletedDeveloper() {
        return new DeveloperView(EntityId.generate(), EMAIL, true, "deleted");
    }

    private LoginVerification activeVerification(EntityId developerId) {
        var hashedCode = CODE.toHashed(String::toString);
        var expiration = LoginCodeExpiration.generate();
        return LoginVerification.create(developerId, hashedCode, expiration);
    }

    // === setup ===
    @BeforeEach
    void setup() {
        loginService = new LoginService(
                repositoryPort,
                authSessionRepositoryPort,
                findDeveloperByEmailUseCase,
                tokenProviderPort,
                publisherPort,
                verifier
        );
    }

    @Test
    void shouldMarkVerificationAsUsedAndReturnTokens() {
        // Given
        var developer = activeDeveloper();
        var verification = activeVerification(developer.id());

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(developer));
        when(repositoryPort.findActiveByDeveloperId(developer.id())).thenReturn(Optional.of(verification));
        when(tokenProviderPort.generateAccessToken(eq(EMAIL), any())).thenReturn(ACCESS_TOKEN);
        when(tokenProviderPort.generateRefreshToken(eq(EMAIL), any())).thenReturn(REFRESH_TOKEN);

        // When
        var result = loginService.execute(command());

        // Then
        assertThat(verification.isUsed()).isTrue();
        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);

        verify(repositoryPort).save(verification);
        verify(authSessionRepositoryPort).save(any(AuthSession.class));
        verifyNoInteractions(publisherPort);
    }

    @Test
    void shouldPublishEventWhenDeveloperIsDeleted() {
        // Given
        var developer = deletedDeveloper();
        var verification = activeVerification(developer.id());

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(developer));
        when(repositoryPort.findActiveByDeveloperId(developer.id())).thenReturn(Optional.of(verification));
        when(tokenProviderPort.generateAccessToken(eq(EMAIL), any())).thenReturn(ACCESS_TOKEN);
        when(tokenProviderPort.generateRefreshToken(eq(EMAIL), any())).thenReturn(REFRESH_TOKEN);

        // When
        loginService.execute(command());

        // Then
        verify(publisherPort).publish(argThat(event ->
                event instanceof DeletedDeveloperLoggedIn e &&
                        e.developerId().equals(developer.id())
        ));
    }

    @Test
    void shouldThrowExceptionWhenDeveloperNotFound() {
        // Given
        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> loginService.execute(command()))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenDeveloperIsBlocked() {
        // Given
        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(blockedDeveloper()));

        // When/Then
        assertThatThrownBy(() -> loginService.execute(command()))
                .isInstanceOf(AccountBlockedException.class);
    }

    @Test
    void shouldThrowExceptionWhenActiveCodeNotFound() {
        // Given
        var developer = activeDeveloper();

        when(findDeveloperByEmailUseCase.execute(EMAIL)).thenReturn(Optional.of(developer));
        when(repositoryPort.findActiveByDeveloperId(developer.id())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> loginService.execute(command()))
                .isInstanceOf(ActiveLoginCodeNotFoundException.class);
    }

}
