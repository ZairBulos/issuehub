package com.issuehub.modules.auth.application.ports.out;

import com.issuehub.modules.auth.domain.models.aggregates.LoginVerification;

public interface LoginVerificationRepositoryPort {
    void replaceActiveVerification(LoginVerification verification);
}
