package com.issuehub.modules.auth.application.ports.out;

import com.issuehub.modules.auth.domain.models.aggregates.LoginVerification;
import com.issuehub.shared.domain.model.EntityId;

import java.util.Optional;

public interface LoginVerificationRepositoryPort {
    Optional<LoginVerification> findActiveByDeveloperId(EntityId developerId);
    void save(LoginVerification verification);
    void replaceActiveVerification(LoginVerification verification);
}
