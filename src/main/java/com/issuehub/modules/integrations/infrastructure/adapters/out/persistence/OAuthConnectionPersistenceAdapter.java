package com.issuehub.modules.integrations.infrastructure.adapters.out.persistence;

import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.mappers.OAuthConnectionMapper;
import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.repositories.OAuthConnectionJpaRepository;
import com.issuehub.shared.domain.model.EntityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuthConnectionPersistenceAdapter implements OAuthConnectionRepositoryPort {

    private final OAuthConnectionJpaRepository repository;
    private final OAuthConnectionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<OAuthConnection> findByDeveloperIdAndProviderAndProviderUserId(EntityId developerId, String provider, String providerUserId) {
        return repository
                .findByDeveloperIdAndProviderAndProviderUserId(developerId.value(), provider, providerUserId)
                .map(mapper::toDomain);
    }

    @Override
    public void save(OAuthConnection connection) {
        repository.save(mapper.toJpaEntity(connection));
    }

}
