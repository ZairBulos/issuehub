package com.issuehub.modules.integrations.application.ports.in;

public record GitHubRepositoryDetails(
        long id,
        String name,
        String fullName,
        String ownerName
) {
}
