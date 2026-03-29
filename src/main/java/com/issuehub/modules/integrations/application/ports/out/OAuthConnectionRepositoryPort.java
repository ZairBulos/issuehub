package com.issuehub.modules.integrations.application.ports.out;

import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.shared.domain.model.EntityId;

import java.util.Optional;

public interface OAuthConnectionRepositoryPort {
    Optional<OAuthConnection> findByDeveloperIdAndProviderAndProviderUserId(
            EntityId developerId,
            String provider,
            String providerUserId
    );
    void save(OAuthConnection connection);
}
