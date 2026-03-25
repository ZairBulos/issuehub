package com.issuehub.modules.integrations.application.dto;

import java.time.Instant;

public record GitHubAccountDto(
        String userId,
        String username,
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
