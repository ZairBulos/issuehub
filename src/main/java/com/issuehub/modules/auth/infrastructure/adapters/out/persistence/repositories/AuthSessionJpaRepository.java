package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories;

import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities.AuthSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthSessionJpaRepository extends JpaRepository<AuthSessionJpaEntity, UUID> {
}
