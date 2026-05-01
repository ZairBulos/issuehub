package com.issuehub.modules.integrations.application.ports.in;

public record ListGitHubRepositoriesQuery(
        String developerEmail,
        String providerUserId,
        int page,
        int pageSize
) {
}
