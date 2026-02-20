package com.issuehub.modules.auth.application.ports.in.internal;

import com.issuehub.modules.auth.application.dto.internal.VerifyEmailCommand;

public interface VerifyEmailUseCase {
    void execute(VerifyEmailCommand command);
}
