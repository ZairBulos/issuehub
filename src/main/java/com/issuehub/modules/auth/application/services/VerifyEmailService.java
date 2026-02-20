package com.issuehub.modules.auth.application.services;

import com.issuehub.modules.auth.application.dto.internal.VerifyEmailCommand;
import com.issuehub.modules.auth.application.ports.in.internal.VerifyEmailUseCase;
import com.issuehub.modules.auth.application.ports.out.EmailVerificationRepositoryPort;
import com.issuehub.shared.domain.events.EmailVerified;
import com.issuehub.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.issuehub.shared.application.ports.out.EventPublisherPort;

import java.time.Instant;
import java.util.function.BiPredicate;

public class VerifyEmailService implements VerifyEmailUseCase {

    private final EmailVerificationRepositoryPort repositoryPort;
    private final EventPublisherPort publisherPort;
    private final BiPredicate<String, String> verifier;

    public VerifyEmailService(EmailVerificationRepositoryPort repositoryPort, EventPublisherPort publisherPort, BiPredicate<String, String> verifier) {
        this.repositoryPort = repositoryPort;
        this.publisherPort = publisherPort;
        this.verifier = verifier;
    }

    @Override
    public void execute(VerifyEmailCommand command) {
        var developerId = command.developerId();
        var code = command.code();

        // RN: Verification code must exist for the developer
        var emailVerification = repositoryPort.findByDeveloperId(developerId)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid verification code"));

        // RN: Validate and mark verification code as used
        emailVerification.use(code, verifier);
        repositoryPort.save(emailVerification);

        publisherPort.publish(new EmailVerified(
                developerId,
                Instant.now()
        ));
    }

}
