package com.issuehub.modules.integrations.application.ports.out;

import com.issuehub.modules.integrations.application.dto.GitHubAccountDto;
import com.issuehub.modules.integrations.application.dto.GitHubRefreshedTokenDto;

public interface GitHubApiPort {
    GitHubAccountDto getAccount(String code);
    GitHubRefreshedTokenDto refreshToken(String refreshToken);
}
