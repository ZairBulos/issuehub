package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.mappers;

import com.issuehub.modules.auth.domain.models.aggregates.LoginVerification;
import com.issuehub.modules.auth.domain.models.valueobjects.HashedLoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCodeExpiration;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities.LoginVerificationJpaEntity;
import com.issuehub.shared.domain.model.EntityId;
import org.springframework.stereotype.Component;

@Component
public class LoginVerificationMapper {

    public LoginVerificationJpaEntity toJpaEntity(LoginVerification verification) {
        return new LoginVerificationJpaEntity(
                verification.getId().value(),
                verification.getDeveloperId().value(),
                verification.getHashedCode().value(),
                verification.getUsedAt(),
                verification.getExpiresAt().value(),
                verification.getCreatedAt()
        );
    }

    public LoginVerification toDomain(LoginVerificationJpaEntity entity) {
        return new LoginVerification(
                new EntityId(entity.getId()),
                new EntityId(entity.getDeveloperId()),
                new HashedLoginCode(entity.getHashedCode()),
                entity.getUsedAt(),
                new LoginCodeExpiration(entity.getExpiresAt()),
                entity.getCreatedAt()
        );
    }

}
