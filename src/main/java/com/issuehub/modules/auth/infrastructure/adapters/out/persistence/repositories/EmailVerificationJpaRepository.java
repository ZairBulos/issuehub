package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories;

import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities.EmailVerificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerificationJpaEntity, UUID> {
    Optional<EmailVerificationJpaEntity> findByDeveloperId(UUID developerId);
}
