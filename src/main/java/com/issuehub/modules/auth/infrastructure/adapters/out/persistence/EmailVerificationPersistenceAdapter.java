package com.issuehub.modules.auth.infrastructure.adapters.out.persistence;

import com.issuehub.modules.auth.application.ports.out.EmailVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.models.aggregates.EmailVerification;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.mappers.EmailVerificationMapper;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.EmailVerificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationPersistenceAdapter implements EmailVerificationRepositoryPort {

    private final EmailVerificationJpaRepository repository;
    private final EmailVerificationMapper mapper;

    @Override
    public void save(EmailVerification verification) {
        repository.save(mapper.toJpaEntity(verification));
    }

}
