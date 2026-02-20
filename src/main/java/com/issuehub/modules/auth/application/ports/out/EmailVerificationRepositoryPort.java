package com.issuehub.modules.auth.application.ports.out;

import com.issuehub.modules.auth.domain.models.aggregates.EmailVerification;
import com.issuehub.shared.domain.model.EntityId;

import java.util.Optional;

public interface EmailVerificationRepositoryPort {
    Optional<EmailVerification> findByDeveloperId(EntityId developerId);
    void save(EmailVerification verification);
}
