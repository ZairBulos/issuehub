package com.issuehub.modules.auth.application.ports.out;

import com.issuehub.modules.auth.domain.models.aggregates.AuthSession;

public interface AuthSessionRepositoryPort {
    void save(AuthSession session);
}
