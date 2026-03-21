package com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.mappers;

import com.issuehub.modules.integrations.domain.models.aggregates.OAuthConnection;
import com.issuehub.modules.integrations.domain.models.enums.OAuthProvider;
import com.issuehub.modules.integrations.domain.models.valueobjects.EncryptedOAuthToken;
import com.issuehub.modules.integrations.domain.models.valueobjects.OAuthTokenExpiration;
import com.issuehub.modules.integrations.domain.models.valueobjects.ProviderUserId;
import com.issuehub.modules.integrations.domain.models.valueobjects.ProviderUsername;
import com.issuehub.modules.integrations.infrastructure.adapters.out.persistence.entities.OAuthConnectionJpaEntity;
import com.issuehub.shared.domain.model.EntityId;
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

    public OAuthConnection toDomain(OAuthConnectionJpaEntity entity) {
        return new OAuthConnection(
                new EntityId(entity.getId()),
                new EntityId(entity.getDeveloperId()),
                OAuthProvider.fromValue(entity.getProvider()),
                new ProviderUserId(entity.getProviderUserId()),
                new ProviderUsername(entity.getProviderUsername()),
                new EncryptedOAuthToken(entity.getEncryptedAccessToken()),
                new EncryptedOAuthToken(entity.getEncryptedRefreshToken()),
                new OAuthTokenExpiration(entity.getAccessTokenExpiresAt()),
                new OAuthTokenExpiration(entity.getRefreshTokenExpiresAt()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
