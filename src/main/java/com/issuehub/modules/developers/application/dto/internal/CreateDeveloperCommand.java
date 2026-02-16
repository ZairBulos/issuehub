package com.issuehub.modules.developers.application.dto.internal;

import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;

public record CreateDeveloperCommand(
        DeveloperEmail email
) {
}
