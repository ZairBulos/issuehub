package com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "oauth_connections")
@Getter @AllArgsConstructor
public class OAuthConnectionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "developer_id", nullable = false)
    private UUID developerId;

    @ColumnTransformer(write = "?::oauth_provider")
    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @Column(name = "provider_username", nullable = false, columnDefinition = "TEXT")
    private String providerUsername;

    @Column(name = "encrypted_access_token", nullable = false, columnDefinition = "TEXT")
    private String encryptedAccessToken;

    @Column(name = "encrypted_refresh_token", nullable = false)
    private String encryptedRefreshToken;

    @Column(name = "access_token_expires_at", nullable = false)
    private Instant accessTokenExpiresAt;

    @Column(name = "refresh_token_expires_at", nullable = false)
    private Instant refreshTokenExpiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OAuthConnectionJpaEntity() {
        // required by JPA
    }

}
