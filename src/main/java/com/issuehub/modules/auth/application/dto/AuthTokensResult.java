package com.issuehub.modules.auth.application.dto;

public record AuthTokensResult(
        String accessToken,
        String refreshToken
) {
}
