package com.issuehub.modules.integrations.application.ports.out;

import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;

public interface OAuthConnectionRepositoryPort {
    void save(OAuthConnection connection);
}
