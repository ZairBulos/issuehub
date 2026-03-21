package com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.mappers;

import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.entities.OAuthConnectionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OAuthConnectionMapper {

    public OAuthConnectionJpaEntity toJpaEntity(OAuthConnection connection) {
        return new OAuthConnectionJpaEntity(
                connection.getId().value(),
                connection.getDeveloperId().value(),
                connection.getProvider().value(),
                connection.getProviderUserId().value(),
                connection.getProviderUsername().value(),
                connection.getEncryptedAccessToken().value(),
                connection.getEncryptedRefreshToken().value(),
                connection.getAccessTokenExpiresAt().value(),
                connection.getRefreshTokenExpiresAt().value(),
                connection.getCreatedAt(),
                connection.getUpdatedAt()
        );
    }

}
