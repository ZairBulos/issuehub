package com.issuehub.modules.integrations.application.dto;

import java.time.Instant;

public record GitHubRefreshedTokenDto(
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
