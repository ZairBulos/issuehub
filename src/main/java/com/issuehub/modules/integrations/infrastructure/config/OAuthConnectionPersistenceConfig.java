package com.issuehub.modules.integrations.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.repositories"
)
public class OAuthConnectionPersistenceConfig {
}
