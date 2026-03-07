package com.issuehub.modules.auth.infrastructure.adapters.out.persistence;

import com.issuehub.modules.auth.application.ports.out.AuthSessionRepositoryPort;
import com.issuehub.modules.auth.domain.models.aggregates.AuthSession;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.mappers.AuthSessionMapper;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.AuthSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthSessionPersistenceAdapter implements AuthSessionRepositoryPort {

    private final AuthSessionJpaRepository repository;
    private final AuthSessionMapper mapper;

    @Override
    public void save(AuthSession session) {
        repository.save(mapper.toJpaEntity(session));
    }

}
