package com.issuehub.modules.integrations.application.ports.out;

import com.issuehub.modules.integrations.application.dto.GitHubAccountDto;

public interface GitHubApiPort {
    GitHubAccountDto getAccount(String code);
}
