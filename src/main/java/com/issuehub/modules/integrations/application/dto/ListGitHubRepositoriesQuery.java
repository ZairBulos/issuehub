package com.issuehub.modules.integrations.application.dto;

public record ListGitHubRepositoriesQuery(
        String developerEmail,
        String providerUserId,
        int page,
        int pageSize
) {
}
