package com.issuehub.modules.developers.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Embeddable
@Getter @Setter @AllArgsConstructor
public class DeveloperProfileEmbeddable {

    @Column(name = "name", nullable = true, length = 255)
    private String name;

    @Column(name = "language", nullable = false, length = 10)
    private String language;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_preferences", nullable = false, columnDefinition = "jsonb")
    private Map<String, Boolean> notificationPreferences;

    protected  DeveloperProfileEmbeddable() {
        // required by JPA
    }

}
