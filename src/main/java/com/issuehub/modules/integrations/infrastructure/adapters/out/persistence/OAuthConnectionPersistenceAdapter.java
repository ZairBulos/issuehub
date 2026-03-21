package com.issuehub.modules.integrations.infrastructure.adapters.out.persistence;

import com.issuehub.modules.integrations.application.ports.out.OAuthConnectionRepositoryPort;
import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.mappers.OAuthConnectionMapper;
import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.repositories.OAuthConnectionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthConnectionPersistenceAdapter implements OAuthConnectionRepositoryPort {

    private final OAuthConnectionJpaRepository repository;
    private final OAuthConnectionMapper mapper;

    @Override
    public void save(OAuthConnection connection) {
        repository.save(mapper.toJpaEntity(connection));
    }

}
