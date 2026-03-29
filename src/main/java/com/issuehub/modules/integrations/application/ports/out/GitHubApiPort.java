package com.issuehub.modules.integrations.application.ports.out;

import com.issuehub.modules.integrations.application.dto.GitHubAccountDto;
import com.issuehub.modules.integrations.application.dto.GitHubRefreshedTokenDto;
import com.issuehub.modules.integrations.application.dto.GitHubRepositoryDto;

import java.util.List;

public interface GitHubApiPort {
    GitHubAccountDto getAccount(String code);
    GitHubRefreshedTokenDto refreshToken(String refreshToken);
    List<GitHubRepositoryDto> getRepositories(String accessToken, String username, int page, int pageSize);
}
