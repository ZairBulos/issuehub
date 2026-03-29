package com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.repositories;

import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.entities.OAuthConnectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OAuthConnectionJpaRepository extends JpaRepository<OAuthConnectionJpaEntity, UUID> {
    Optional<OAuthConnectionJpaEntity> findByDeveloperIdAndProviderAndProviderUserId(
            UUID developerId,
            String provider,
            String providerUserId
    );
}
