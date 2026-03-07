package com.issuehub.shared.infrastructure.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        String issuer,
        String audience,
        Duration accessTokenExpiration,
        Duration refreshTokenExpiration
) {
}
