package com.issuehub.modules.developers.application.ports.in;

import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.shared.domain.model.EntityId;

import java.util.Optional;

public interface FindDeveloperUseCase {
    Optional<DeveloperView> execute(EntityId developerId);
}
