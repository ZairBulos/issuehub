package com.issuehub.modules.integrations.application.ports.in;

import com.issuehub.modules.integrations.application.dto.GitHubRepositoryDto;
import com.issuehub.modules.integrations.application.dto.ListGitHubRepositoriesQuery;

import java.util.List;

public interface ListGitHubRepositoriesUseCase {
    List<GitHubRepositoryDto> execute(ListGitHubRepositoriesQuery query);
}
