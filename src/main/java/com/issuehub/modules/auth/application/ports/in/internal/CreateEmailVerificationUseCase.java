package com.issuehub.modules.auth.application.ports.in.internal;

import com.issuehub.shared.domain.events.DeveloperCreated;

public interface CreateEmailVerificationUseCase {
    void execute(DeveloperCreated event);
}
