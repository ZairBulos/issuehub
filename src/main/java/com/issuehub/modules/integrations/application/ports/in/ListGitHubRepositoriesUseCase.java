package com.issuehub.modules.integrations.application.ports.in;

import java.util.List;

public interface ListGitHubRepositoriesUseCase {
    List<GitHubRepositoryDetails> execute(ListGitHubRepositoriesQuery query);
}
