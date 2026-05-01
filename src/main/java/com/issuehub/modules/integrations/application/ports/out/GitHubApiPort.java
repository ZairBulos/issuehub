package com.issuehub.modules.integrations.application.ports.out;

import com.issuehub.modules.integrations.application.ports.in.GitHubRepositoryDetails;

import java.util.List;

public interface GitHubApiPort {
    GitHubAccountDetails getAccount(String code);
    GitHubRefreshedTokenDetails refreshToken(String refreshToken);
    List<GitHubRepositoryDetails> getRepositories(String accessToken, String username, int page, int pageSize);
}
