package com.issuehub.shared.application.ports.security;

import com.issuehub.shared.application.dto.DecodedToken;

import java.util.Map;

public interface TokenProviderPort {
    String generateAccessToken(String subject, Map<String, Object> claims);
    String generateRefreshToken(String subject, Map<String, Object> claims);
    DecodedToken verifyAccessToken(String token);
    DecodedToken verifyRefreshToken(String token);
}
