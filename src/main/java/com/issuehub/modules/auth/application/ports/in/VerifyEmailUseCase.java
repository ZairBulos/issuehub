package com.issuehub.modules.auth.application.ports.in;

import com.issuehub.modules.auth.application.dto.VerifyEmailCommand;

public interface VerifyEmailUseCase {
    void execute(VerifyEmailCommand command);
}
