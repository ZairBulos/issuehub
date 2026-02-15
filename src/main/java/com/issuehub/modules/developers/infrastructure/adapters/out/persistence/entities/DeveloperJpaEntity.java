package com.issuehub.modules.developers.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "developers")
@Getter @Setter @AllArgsConstructor
public class DeveloperJpaEntity {

    @Id
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @ColumnTransformer(write = "?::developer_status")
    @Column(name = "status", nullable = false)
    private String status;

    @Embedded
    private DeveloperProfileEmbeddable profile;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected DeveloperJpaEntity() {
        // required by JPA
    }

}
