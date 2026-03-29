package com.issuehub.modules.integrations.application.dto;

public record GitHubRepositoryDto(
        long id,
        String name,
        String fullName,
        String ownerName
) {
}
