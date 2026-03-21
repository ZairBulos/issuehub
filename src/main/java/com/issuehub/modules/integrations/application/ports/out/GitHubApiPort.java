package com.issuehub.modules.integrations.application.ports.out;

import com.issuehub.modules.integrations.application.dto.GitHubOAuthResponse;

public interface GitHubApiPort {
    GitHubOAuthResponse exchangeCode(String code);
}
