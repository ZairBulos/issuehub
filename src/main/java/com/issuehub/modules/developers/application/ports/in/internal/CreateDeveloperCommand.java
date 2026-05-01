package com.issuehub.modules.developers.application.ports.in.internal;

import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;

public record CreateDeveloperCommand(DeveloperEmail email) {
}
