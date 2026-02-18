package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_verifications")
@Getter @Setter @AllArgsConstructor
public class EmailVerificationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "developer_id", nullable = false)
    private UUID developerId;

    @Column(name = "hashed_code", nullable = false, length = 255)
    private String hashedCode;

    @Column(name = "used_at", nullable = true)
    private Instant usedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected EmailVerificationJpaEntity() {
        // required by JPA
    }

}
