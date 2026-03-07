package com.issuehub.modules.auth.application.ports.in;

import com.issuehub.modules.auth.application.dto.AuthTokensResult;
import com.issuehub.modules.auth.application.dto.LoginCommand;

public interface LoginUseCase {
    AuthTokensResult execute(LoginCommand command);
}
