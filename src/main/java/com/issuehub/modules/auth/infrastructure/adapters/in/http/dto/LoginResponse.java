package com.issuehub.modules.auth.infrastructure.adapters.in.http.dto;

import com.issuehub.modules.auth.application.dto.AuthTokensResult;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {

    public static LoginResponse from(AuthTokensResult result) {
        return new LoginResponse(result.accessToken(), result.refreshToken());
    }

}
