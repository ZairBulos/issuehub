package com.issuehub.shared.application.dto;

import java.time.Instant;
import java.util.Map;

public record DecodedToken(
        String jti,
        String subject,
        Map<String, Object> claims,
        Instant issuedAt,
        Instant expiresAt
) {
}
