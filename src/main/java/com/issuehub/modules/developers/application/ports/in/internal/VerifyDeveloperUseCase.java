package com.issuehub.modules.developers.application.ports.in.internal;

import com.issuehub.shared.domain.model.EntityId;

public interface VerifyDeveloperUseCase {
    void execute(EntityId developerId);
}
