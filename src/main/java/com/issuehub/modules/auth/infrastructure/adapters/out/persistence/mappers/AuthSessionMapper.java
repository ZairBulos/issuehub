package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.mappers;

import com.issuehub.modules.auth.domain.exceptions.InvalidIpAddressException;
import com.issuehub.modules.auth.domain.models.aggregates.AuthSession;
import com.issuehub.modules.auth.domain.models.valueobjects.HashedRefreshToken;
import com.issuehub.modules.auth.domain.models.valueobjects.IpAddress;
import com.issuehub.modules.auth.domain.models.valueobjects.RefreshTokenExpiration;
import com.issuehub.modules.auth.domain.models.valueobjects.UserAgent;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities.AuthSessionJpaEntity;
import com.issuehub.shared.domain.model.EntityId;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class AuthSessionMapper {

    public AuthSessionJpaEntity toJpaEntity(AuthSession session) {
        try {
            return new AuthSessionJpaEntity(
                    session.getId().value(),
                    session.getDeveloperId().value(),
                    session.getHashedToken().value(),
                    session.getExpiresAt().value(),
                    session.isRevoked(),
                    InetAddress.getByName(session.getIpAddress().value()),
                    session.getUserAgent().value(),
                    session.getCreatedAt(),
                    session.getUpdatedAt()
            );
        } catch (UnknownHostException e) {
            throw new InvalidIpAddressException("Invalid IP address " + session.getIpAddress().value());
        }
    }

    public AuthSession toDomain(AuthSessionJpaEntity entity) {
        return new AuthSession(
                new EntityId(entity.getId()),
                new EntityId(entity.getDeveloperId()),
                new HashedRefreshToken(entity.getHashedToken()),
                new RefreshTokenExpiration(entity.getExpiresAt()),
                entity.getRevoked(),
                new IpAddress(entity.getIpAddress().getHostAddress()),
                new UserAgent(entity.getUserAgent()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
