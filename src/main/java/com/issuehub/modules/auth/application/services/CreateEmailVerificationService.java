package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.ports.in.internal.CreateEmailVerificationUseCase;
import com.issuehub.modules.auth.application.ports.out.EmailVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.events.EmailVerificationCreated;
import com.issuehub.modules.auth.domain.models.aggregates.EmailVerification;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationExpiration;
import com.issuehub.shared.domain.events.DeveloperCreated;
import com.issuehub.shared.application.ports.out.EventPublisherPort;

import java.time.Instant;
import java.util.function.Function;

public class CreateEmailVerificationService implements CreateEmailVerificationUseCase {

    private final EmailVerificationRepositoryPort repositoryPort;
    private final EventPublisherPort publisherPort;
    private final Function<String, String> hasher;

    public CreateEmailVerificationService(
            EmailVerificationRepositoryPort repositoryPort,
            EventPublisherPort publisherPort,
            Function<String, String> hasher
    ) {
        this.repositoryPort = repositoryPort;
        this.publisherPort = publisherPort;
        this.hasher = hasher;
    }

    @Override
    public void execute(DeveloperCreated event) {
        // RN: Generate and hash verification code
        var code = VerificationCode.generate();
        var hashedCode = code.toHashed(hasher);
        var expiresAt = VerificationExpiration.generate();

        var emailVerification = EmailVerification.create(event.developerId(), hashedCode, expiresAt);
        repositoryPort.save(emailVerification);

        publisherPort.publish(new EmailVerificationCreated(
                emailVerification.getId(),
                emailVerification.getDeveloperId(),
                event.developerEmail(),
                code.value(),
                Instant.now()
        ));
    }

}
