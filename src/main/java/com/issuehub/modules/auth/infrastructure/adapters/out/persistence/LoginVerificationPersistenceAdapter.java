package com.issuehub.modules.auth.infrastructure.adapters.out.persistence;

import com.issuehub.modules.auth.application.ports.out.LoginVerificationRepositoryPort;
import com.issuehub.modules.auth.domain.models.aggregates.LoginVerification;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.mappers.LoginVerificationMapper;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories.LoginVerificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LoginVerificationPersistenceAdapter implements LoginVerificationRepositoryPort {

    private final LoginVerificationJpaRepository repository;
    private final LoginVerificationMapper mapper;

    @Override
    @Transactional
    public void replaceActiveVerification(LoginVerification verification) {
        repository.disableActiveVerification(verification.getDeveloperId().value(), Instant.now());
        repository.save(mapper.toJpaEntity(verification));
    }

}
