package com.issuehub.modules.integrations.application.ports.out;

import java.time.Instant;

public record GitHubAccountDetails(
        String userId,
        String username,
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
