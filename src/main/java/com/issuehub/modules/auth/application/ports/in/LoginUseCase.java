package com.issuehub.modules.auth.application.ports.in;

public interface LoginUseCase {
    AuthTokensResult execute(LoginCommand command);
}
