package com.issuehub.shared.infrastructure.adapters.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.issuehub.shared.application.dto.DecodedToken;
import com.issuehub.shared.application.exceptions.InvalidTokenException;
import com.issuehub.shared.application.ports.security.TokenProviderPort;
import com.issuehub.shared.infrastructure.config.security.JwtProperties;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProviderPort {

    private static final String ACCESS_TOKEN_TYPE = "ACCESS_JWT";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH_JWT";

    private final JwtProperties jwtProperties;
    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtProperties.secret());
        verifier = JWT.require(algorithm)
                .withIssuer(jwtProperties.issuer())
                .withAudience(jwtProperties.audience())
                .build();
    }

    // === generation ===
    @Override
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, ACCESS_TOKEN_TYPE, jwtProperties.accessTokenExpiration());
    }

    @Override
    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, REFRESH_TOKEN_TYPE, jwtProperties.refreshTokenExpiration());
    }

    private String buildToken(
            @Nonnull String subject,
            @Nonnull Map<String, Object> claims,
            @Nonnull String tokenType,
            @Nonnull Duration ttl
    ) {
        var now = Instant.now();

        return JWT.create()
                .withHeader(Map.of(
                        "alg", "HS256",
                        "typ", tokenType
                ))
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer(jwtProperties.issuer())
                .withAudience(jwtProperties.audience())
                .withSubject(subject)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(ttl))
                .withNotBefore(now)
                .withClaim("claims", claims)
                .sign(algorithm);
    }

    // === verification ===
    @Override
    public DecodedToken verifyAccessToken(String token) {
        return verify(token, ACCESS_TOKEN_TYPE);
    }

    @Override
    public DecodedToken verifyRefreshToken(String token) {
        return verify(token, REFRESH_TOKEN_TYPE);
    }

    private DecodedToken verify(String token, String expectedType) {
        try {
            var typ = JWT.decode(token).getHeaderClaim("typ").asString();
            if (!expectedType.equals(typ))
                throw new InvalidTokenException("Invalid token type: expected '%s' but got '%s'".formatted(expectedType, typ));

            var decoded = verifier.verify(token);

            return new DecodedToken(
                    decoded.getId(),
                    decoded.getSubject(),
                    decoded.getClaim("claims").asMap(),
                    decoded.getIssuedAtAsInstant(),
                    decoded.getExpiresAtAsInstant()
            );
        } catch (JWTDecodeException e) {
            throw new InvalidTokenException("Malformed token", e);
        } catch (TokenExpiredException e) {
            throw new InvalidTokenException("Token has expired", e);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Token verification failed", e);
        }
    }

}
