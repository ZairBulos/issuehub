package com.issuehub.modules.developers.application.ports.in;

import com.issuehub.shared.domain.models.EntityId;

import java.util.Optional;

public interface FindDeveloperByIdUseCase {
    Optional<DeveloperView> execute(EntityId developerId);
}
