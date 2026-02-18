package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.mappers;

import com.issuehub.modules.auth.domain.models.aggregates.EmailVerification;
import com.issuehub.modules.auth.domain.models.valueobjects.HashedVerificationCode;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationExpiration;
import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities.EmailVerificationJpaEntity;
import com.issuehub.shared.domain.model.EntityId;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationMapper {

    public EmailVerificationJpaEntity toJpaEntity(EmailVerification verification) {
        return new EmailVerificationJpaEntity(
                verification.getId().value(),
                verification.getDeveloperId().value(),
                verification.getHashedCode().value(),
                verification.getUsedAt(),
                verification.getExpiresAt().value(),
                verification.getCreatedAt()
        );
    }

    public EmailVerification toDomain(EmailVerificationJpaEntity entity) {
        return new EmailVerification(
                new EntityId(entity.getId()),
                new EntityId(entity.getDeveloperId()),
                new HashedVerificationCode(entity.getHashedCode()),
                entity.getUsedAt(),
                new VerificationExpiration(entity.getExpiresAt()),
                entity.getCreatedAt()
        );
    }

}
