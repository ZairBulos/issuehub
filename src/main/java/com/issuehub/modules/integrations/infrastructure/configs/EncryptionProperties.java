package com.issuehub.modules.integrations.infrastructure.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.encryption")
public record EncryptionProperties(
        String secretKey
) {
}
