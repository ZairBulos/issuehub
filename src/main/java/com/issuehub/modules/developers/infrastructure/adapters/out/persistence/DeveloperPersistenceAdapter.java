package com.issuehub.modules.developers.infrastructure.adapters.out.persistence;

import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.mappers.DeveloperMapper;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories.DeveloperJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeveloperPersistenceAdapter {

    private final DeveloperJpaRepository repository;
    private final DeveloperMapper mapper;

}
