package com.issuehub.modules.integrations.domain.models.aggregates;

import com.issuehub.modules.integrations.domain.models.enums.OAuthProvider;
import com.issuehub.modules.integrations.domain.models.valueobjects.*;
import com.issuehub.shared.domain.model.AggregateRoot;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;

public class OAuthConnection extends AggregateRoot {

    private final EntityId id;
    private final EntityId developerId;
    private final OAuthProvider provider;
    private final ProviderUserId providerUserId;
    private final ProviderUsername providerUsername;
    private EncryptedOAuthToken encryptedAccessToken;
    private EncryptedOAuthToken encryptedRefreshToken;
    private OAuthTokenExpiration accessTokenExpiresAt;
    private OAuthTokenExpiration refreshTokenExpiresAt;
    private final Instant createdAt;
    private Instant updatedAt;

    // === Factory Method ===
    public static OAuthConnection createGitHub(
            EntityId developerId,
            ProviderUserId providerUserId,
            ProviderUsername providerUsername,
            EncryptedOAuthToken encryptedAccessToken,
            EncryptedOAuthToken encryptedRefreshToken,
            OAuthTokenExpiration accessTokenExpiresAt,
            OAuthTokenExpiration refreshTokenExpiresAt
    ) {
        return new OAuthConnection(
                EntityId.generate(),
                developerId,
                OAuthProvider.GITHUB,
                providerUserId,
                providerUsername,
                encryptedAccessToken,
                encryptedRefreshToken,
                accessTokenExpiresAt,
                refreshTokenExpiresAt,
                Instant.now(),
                Instant.now()
        );
    }

    public static OAuthConnection createGitLab(
            EntityId developerId,
            ProviderUserId providerUserId,
            ProviderUsername providerUsername,
            EncryptedOAuthToken encryptedAccessToken,
            EncryptedOAuthToken encryptedRefreshToken,
            OAuthTokenExpiration accessTokenExpiresAt,
            OAuthTokenExpiration refreshTokenExpiresAt
    ) {
        return new OAuthConnection(
                EntityId.generate(),
                developerId,
                OAuthProvider.GITLAB,
                providerUserId,
                providerUsername,
                encryptedAccessToken,
                encryptedRefreshToken,
                accessTokenExpiresAt,
                refreshTokenExpiresAt,
                Instant.now(),
                Instant.now()
        );
    }

    // === Constructor ===
    public OAuthConnection(
            EntityId id,
            EntityId developerId,
            OAuthProvider provider,
            ProviderUserId providerUserId,
            ProviderUsername providerUsername,
            EncryptedOAuthToken encryptedAccessToken,
            EncryptedOAuthToken encryptedRefreshToken,
            OAuthTokenExpiration accessTokenExpiresAt,
            OAuthTokenExpiration refreshTokenExpiresAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.developerId = developerId;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.providerUsername = providerUsername;
        this.encryptedAccessToken = encryptedAccessToken;
        this.encryptedRefreshToken = encryptedRefreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // === Checks ===
    public boolean isAccessTokenExpired(Instant now) {
        return accessTokenExpiresAt.isExpired(now);
    }

    public boolean isRefreshTokenExpired(Instant now) {
        return refreshTokenExpiresAt.isExpired(now);
    }

    // === Actions ===
    public void refreshTokens(
            EncryptedOAuthToken encryptedAccessToken,
            EncryptedOAuthToken encryptedRefreshToken,
            OAuthTokenExpiration accessTokenExpiresAt,
            OAuthTokenExpiration refreshTokenExpiresAt
    ) {
        this.encryptedAccessToken = encryptedAccessToken;
        this.encryptedRefreshToken = encryptedRefreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.updatedAt = Instant.now();
    }

    // === Getters ===
    public EntityId getId() {
        return id;
    }

    public EntityId getDeveloperId() {
        return developerId;
    }

    public OAuthProvider getProvider() {
        return provider;
    }

    public ProviderUserId getProviderUserId() {
        return providerUserId;
    }

    public ProviderUsername getProviderUsername() {
        return providerUsername;
    }

    public EncryptedOAuthToken getEncryptedAccessToken() {
        return encryptedAccessToken;
    }

    public EncryptedOAuthToken getEncryptedRefreshToken() {
        return encryptedRefreshToken;
    }

    public OAuthTokenExpiration getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public OAuthTokenExpiration getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
