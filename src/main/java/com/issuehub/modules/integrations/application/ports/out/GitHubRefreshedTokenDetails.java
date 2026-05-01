package com.issuehub.modules.integrations.application.ports.out;

import java.time.Instant;

public record GitHubRefreshedTokenDetails(
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
