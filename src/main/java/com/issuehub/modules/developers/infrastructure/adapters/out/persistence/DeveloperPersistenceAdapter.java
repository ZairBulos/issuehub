package com.issuehub.modules.developers.infrastructure.adapters.out.persistence;

import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.mappers.DeveloperMapper;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeveloperPersistenceAdapter implements DeveloperRepositoryPort {

    private final DeveloperJpaRepository repository;
    private final DeveloperMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Developer> findByEmail(DeveloperEmail email) {
        return repository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public void save(Developer developer) {
        repository.save(mapper.toJpaEntity(developer));
    }

}
