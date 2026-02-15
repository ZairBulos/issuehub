package com.issuehub.modules.developers.infrastructure.adapters.out.persistence.mappers;

import com.issuehub.modules.developers.domain.models.aggregates.Developer;
import com.issuehub.modules.developers.domain.models.enums.DeveloperStatus;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperEmail;
import com.issuehub.modules.developers.domain.models.valueobjects.DeveloperProfile;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.entities.DeveloperJpaEntity;
import com.issuehub.modules.developers.infrastructure.adapters.out.persistence.entities.DeveloperProfileEmbeddable;
import com.issuehub.shared.domain.model.EntityId;
import org.springframework.stereotype.Component;

@Component
public class DeveloperMapper {

    public DeveloperJpaEntity toJpaEntity(Developer developer) {
        return new DeveloperJpaEntity(
                developer.getId().value(),
                developer.getEmail().value(),
                developer.getVerified(),
                developer.getStatus().value(),
                toJpaProfile(developer.getProfile()),
                developer.getCreatedAt(),
                developer.getUpdatedAt()
        );
    }

    public Developer toDomain(DeveloperJpaEntity entity) {
        return new Developer(
                new EntityId(entity.getId()),
                new DeveloperEmail(entity.getEmail()),
                entity.getIsVerified(),
                DeveloperStatus.fromValue(entity.getStatus()),
                toDomainProfile(entity.getProfile()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private DeveloperProfileEmbeddable toJpaProfile(DeveloperProfile profile) {
        return new DeveloperProfileEmbeddable(
                profile.name(),
                profile.language(),
                profile.timezone(),
                profile.notificationPreferences()
        );
    }

    private DeveloperProfile toDomainProfile(DeveloperProfileEmbeddable entity) {
        return new DeveloperProfile(
                entity.getName(),
                entity.getLanguage(),
                entity.getTimezone(),
                entity.getNotificationPreferences()
        );
    }

}
