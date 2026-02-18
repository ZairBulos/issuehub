package com.issuehub.modules.auth.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories"
)
public class AuthPersistenceConfig {
}
