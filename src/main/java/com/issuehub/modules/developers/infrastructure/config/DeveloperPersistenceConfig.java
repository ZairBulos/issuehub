package com.issuehub.modules.developers.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.issuehub.modules.developers.infrastructure.adapters.out.persistence.repositories"
)
public class DeveloperPersistenceConfig {
}
