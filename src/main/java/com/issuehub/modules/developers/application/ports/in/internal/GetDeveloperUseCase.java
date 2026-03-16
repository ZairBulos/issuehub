package com.issuehub.modules.developers.application.ports.in.internal;

import com.issuehub.modules.developers.application.dto.internal.DeveloperDTO;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;

public interface GetDeveloperUseCase {
    DeveloperDTO execute(DeveloperEmail developerEmail);
}
