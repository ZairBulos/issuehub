package com.issuehub.modules.auth.application.ports.out;

import com.issuehub.modules.auth.domain.models.aggregates.EmailVerification;

public interface EmailVerificationRepositoryPort {
    void save(EmailVerification verification);
}
