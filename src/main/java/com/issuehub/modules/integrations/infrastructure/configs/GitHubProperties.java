package com.issuehub.modules.integrations.infrastructure.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.github")
public record GitHubProperties(
        String clientId,
        String clientSecret,
        String callbackUrl
) {
}
