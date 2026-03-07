package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @AllArgsConstructor
public class AuthSessionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "developer_id", nullable = false)
    private UUID developerId;

    @Column(name = "hashed_token", nullable = false, length = 255)
    private String hashedToken;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    @Column(name = "ip_address", nullable = false, updatable = false, columnDefinition = "INET")
    private InetAddress ipAddress;

    @Column(name = "user_agent", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthSessionJpaEntity() {
        // required by JPA
    }

}
