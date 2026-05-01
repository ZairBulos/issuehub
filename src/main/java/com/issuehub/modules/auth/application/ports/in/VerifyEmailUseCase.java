package com.issuehub.modules.auth.application.ports.in;

public interface VerifyEmailUseCase {
    void execute(VerifyEmailCommand command);
}
