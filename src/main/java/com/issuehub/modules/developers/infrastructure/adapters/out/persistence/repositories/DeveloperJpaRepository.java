package com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories;

import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.entities.DeveloperJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeveloperJpaRepository extends JpaRepository<DeveloperJpaEntity, UUID> {
    Optional<DeveloperJpaEntity> findByEmail(String email);
}
