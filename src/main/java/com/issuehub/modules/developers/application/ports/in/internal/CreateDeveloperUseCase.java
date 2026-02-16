package com.issuehub.modules.developers.application.ports.in.internal;

import com.issuehub.modules.developers.application.dto.internal.CreateDeveloperCommand;

public interface CreateDeveloperUseCase {
    void execute(CreateDeveloperCommand command);
}
