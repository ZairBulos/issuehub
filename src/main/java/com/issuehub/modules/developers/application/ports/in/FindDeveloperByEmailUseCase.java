package com.issuehub.modules.developers.application.ports.in;

import com.issuehub.modules.developers.application.dto.DeveloperView;

import java.util.Optional;

public interface FindDeveloperByEmailUseCase {
    Optional<DeveloperView> execute(String developerEmail);
}
