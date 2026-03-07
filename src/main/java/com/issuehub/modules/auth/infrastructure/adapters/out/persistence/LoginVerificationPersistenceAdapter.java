package com.issuehub.modules.auth.infrastructure.adapters.out.persistence;

import com.issuehub.modules.auth.application.ports.out.LoginVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.models.aggregates.LoginVerification;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.mappers.LoginVerificationMapper;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.LoginVerificationJpaRepository;
import com.issuehub.shared.domain.model.EntityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginVerificationPersistenceAdapter implements LoginVerificationRepositoryPort {

    private final LoginVerificationJpaRepository repository;
    private final LoginVerificationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<LoginVerification> findActiveByDeveloperId(EntityId developerId) {
        return repository.findActiveByDeveloperId(developerId.value())
                .map(mapper::toDomain);
    }

    @Override
    public void save(LoginVerification verification) {
        repository.save(mapper.toJpaEntity(verification));
    }

    @Override
    @Transactional
    public void replaceActiveVerification(LoginVerification verification) {
        repository.disableActiveVerification(verification.getDeveloperId().value(), Instant.now());
        repository.save(mapper.toJpaEntity(verification));
    }

}
