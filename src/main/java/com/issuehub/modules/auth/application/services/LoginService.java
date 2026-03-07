package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.dto.AuthTokensResult;
import com.issuehub.modules.auth.application.dto.LoginCommand;
import com.issuehub.modules.auth.application.exceptions.AccountBlockedException;
import com.issuehub.modules.auth.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.auth.application.exceptions.ActiveLoginCodeNotFoundException;
import com.issuehub.modules.auth.application.ports.in.LoginUseCase;
import com.issuehub.modules.auth.application.ports.out.AuthSessionRepositoryPort;
import com.issuehub.modules.auth.application.ports.out.LoginVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.models.aggregates.AuthSession;
import com.issuehub.modules.auth.domain.models.valueobjects.RefreshToken;
import com.issuehub.modules.auth.domain.models.valueobjects.RefreshTokenExpiration;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.shared.application.ports.out.EventPublisherPort;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import com.issuehub.shared.domain.events.DeletedDeveloperLoggedIn;

import java.time.Instant;
import java.util.Map;
import java.util.function.BiPredicate;

public class LoginService implements LoginUseCase {

    private final LoginVerificationRepositoryPort repositoryPort;
    private final AuthSessionRepositoryPort authSessionRepositoryPort;
    private final FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;
    private final TokenProviderPort tokenProviderPort;
    private final EventPublisherPort publisherPort;
    private final BiPredicate<String, String> verifier;

    public LoginService(
            LoginVerificationRepositoryPort repositoryPort,
            AuthSessionRepositoryPort authSessionRepositoryPort,
            FindDeveloperByEmailUseCase findDeveloperByEmailUseCase,
            TokenProviderPort tokenProviderPort,
            EventPublisherPort publisherPort,
            BiPredicate<String, String> verifier
    ) {
        this.repositoryPort = repositoryPort;
        this.authSessionRepositoryPort = authSessionRepositoryPort;
        this.findDeveloperByEmailUseCase = findDeveloperByEmailUseCase;
        this.tokenProviderPort = tokenProviderPort;
        this.publisherPort = publisherPort;
        this.verifier = verifier;
    }

    @Override
    public AuthTokensResult execute(LoginCommand command) {
        var developerEmail = command.developerEmail();
        var code = command.code();
        var ipAddress = command.ipAddress();
        var userAgent = command.userAgent();

        // RN: Developer must exist
        var developer = findDeveloperByEmailUseCase.execute(developerEmail)
                .orElseThrow(() -> new AccountNotFoundException("Invalid credentials"));

        // RN: Developer must not be blocked
        if (developer.isBlocked())
            throw new AccountBlockedException("Account is blocked: " + developerEmail);

        // RN: Active code must exist for developer
        var loginVerification = repositoryPort
                .findActiveByDeveloperId(developer.id())
                .orElseThrow(() -> new ActiveLoginCodeNotFoundException("No active code found"));

        // RN: Validate and mark verification code as used
        loginVerification.use(code, verifier);
        repositoryPort.save(loginVerification);

        // Generate tokens
        Map<String, Object> claims = Map.of("developerId", developer.id().value().toString());
        var accessToken = tokenProviderPort.generateAccessToken(developer.email(), claims);
        var refreshToken = tokenProviderPort.generateRefreshToken(developer.email(), claims);

        // RN: Persist refresh token as a new auth session
        var hashedRefreshToken = new RefreshToken(refreshToken).toHashed();
        var refreshTokenExp = RefreshTokenExpiration.generate();
        var session = AuthSession.create(developer.id(), hashedRefreshToken, refreshTokenExp, ipAddress, userAgent);
        authSessionRepositoryPort.save(session);

        // RN: Reactivate deleted developers
        if (developer.isDeleted())
            publisherPort.publish(new DeletedDeveloperLoggedIn(developer.id(), Instant.now()));

        return new AuthTokensResult(accessToken, refreshToken);
    }

}
