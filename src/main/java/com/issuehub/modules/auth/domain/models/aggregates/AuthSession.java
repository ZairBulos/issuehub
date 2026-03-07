package com.issuehub.modules.auth.domain.models.aggregates;

import com.issuehub.modules.auth.domain.exceptions.AuthSessionAlreadyRevokedException;
import com.issuehub.modules.auth.domain.models.valueobjects.HashedRefreshToken;
import com.issuehub.modules.auth.domain.models.valueobjects.IpAddress;
import com.issuehub.modules.auth.domain.models.valueobjects.RefreshTokenExpiration;
import com.issuehub.modules.auth.domain.models.valueobjects.UserAgent;
import com.issuehub.shared.domain.model.AggregateRoot;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;

public class AuthSession extends AggregateRoot {

    private final EntityId id;
    private final EntityId developerId;
    private final HashedRefreshToken hashedToken;
    private final RefreshTokenExpiration expiresAt;
    private Boolean revoked;
    private final IpAddress ipAddress;
    private final UserAgent userAgent;
    private final Instant createdAt;
    private Instant updatedAt;

    // === Factory Method ===
    public static AuthSession create(
            EntityId developerId,
            HashedRefreshToken hashedToken,
            RefreshTokenExpiration expiresAt,
            IpAddress ipAddress,
            UserAgent userAgent
    ) {
       return new AuthSession(
               EntityId.generate(),
               developerId,
               hashedToken,
               expiresAt,
               false,
               ipAddress,
               userAgent,
               Instant.now(),
               Instant.now()
       );
    }

    // === Constructor ===
    public AuthSession(EntityId id, EntityId developerId, HashedRefreshToken hashedToken, RefreshTokenExpiration expiresAt, Boolean revoked, IpAddress ipAddress, UserAgent userAgent, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.developerId = developerId;
        this.hashedToken = hashedToken;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // === Checks ===
    public boolean isValid(Instant now) {
        return !isRevoked() && !isExpired(now);
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isExpired(now);
    }

    // === Actions ===
    public void revoke() {
        if (isRevoked())
            throw new AuthSessionAlreadyRevokedException("Auth session already revoked");

        this.revoked = true;
        this.updatedAt = Instant.now();
    }

    // === Getters ===
    public EntityId getId() {
        return id;
    }

    public EntityId getDeveloperId() {
        return developerId;
    }

    public HashedRefreshToken getHashedToken() {
        return hashedToken;
    }

    public RefreshTokenExpiration getExpiresAt() {
        return expiresAt;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public IpAddress getIpAddress() {
        return ipAddress;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
