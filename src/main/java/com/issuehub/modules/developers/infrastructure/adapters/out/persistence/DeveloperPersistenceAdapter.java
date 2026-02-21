package com.issuehub.modules.developers.infrastructure.adapters.out.persistence;

import com.issuehub.modules.developers.application.dto.DeveloperView;
import com.issuehub.modules.developers.application.ports.out.DeveloperRepositoryPort;
import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.mappers.DeveloperMapper;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import com.issuehub.shared.domain.model.EntityId;
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
    @Transactional(readOnly = true)
    public Optional<Developer> findById(EntityId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeveloperView> findViewById(EntityId id) {
        return repository.findProjectedById(id.value())
                .map(mapper::toView);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeveloperView> findViewByEmail(DeveloperEmail email) {
        return repository.findProjectedByEmail(email.value())
                .map(mapper::toView);
    }

    @Override
    public void save(Developer developer) {
        repository.save(mapper.toJpaEntity(developer));
    }

}
