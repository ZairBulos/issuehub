package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.exceptions.AccountBlockedException;
import com.issuehub.modules.auth.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.auth.application.exceptions.AccountNotVerifiedException;
import com.issuehub.modules.auth.application.ports.in.internal.RequestLoginUseCase;
import com.issuehub.modules.auth.application.ports.out.LoginVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.events.LoginVerificationCreated;
import com.issuehub.modules.auth.domain.models.aggregates.LoginVerification;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCodeExpiration;
import com.issuehub.modules.developers.application.ports.in.FindDeveloperByEmailUseCase;
import com.issuehub.shared.application.ports.out.EventPublisherPort;

import java.time.Instant;
import java.util.function.Function;

public class RequestLoginService implements RequestLoginUseCase {

    private final LoginVerificationRepositoryPort repositoryPort;
    private final FindDeveloperByEmailUseCase findDeveloperByEmailUseCase;
    private final EventPublisherPort publisherPort;
    private final Function<String, String> hasher;

    public RequestLoginService(
            LoginVerificationRepositoryPort repositoryPort,
            FindDeveloperByEmailUseCase findDeveloperByEmailUseCase,
            EventPublisherPort publisherPort,
            Function<String, String> hasher
    ) {
        this.repositoryPort = repositoryPort;
        this.findDeveloperByEmailUseCase = findDeveloperByEmailUseCase;
        this.publisherPort = publisherPort;
        this.hasher = hasher;
    }

    @Override
    public void execute(String email) {
        // RN: Developer must exist
        var developer = findDeveloperByEmailUseCase.execute(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found " + email));

        // RN: Developer must not be blocked
        if (developer.isBlocked())
            throw new AccountBlockedException("Account is blocked " + email);

        // RN: Developer must be verified
        if (!developer.isVerified())
            throw new AccountNotVerifiedException("Account is not verified " + email);

        // RN: Generate and hash verification code
        var code = LoginCode.generate();
        var hashedCode = code.toHashed(hasher);
        var expiresAt = LoginCodeExpiration.generate();

        // RN: Any existing code must be invalidated and replaced
        var loginVerification = LoginVerification.create(developer.id(), hashedCode, expiresAt);
        repositoryPort.replaceActiveVerification(loginVerification);

        publisherPort.publish(new LoginVerificationCreated(
                loginVerification.getId(),
                developer.id(),
                developer.email(),
                code.value(),
                Instant.now()
        ));
    }

}
