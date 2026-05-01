package com.issuehub.modules.auth.application.ports.in;

public record AuthTokensResult(
        String accessToken,
        String refreshToken
) {
}
