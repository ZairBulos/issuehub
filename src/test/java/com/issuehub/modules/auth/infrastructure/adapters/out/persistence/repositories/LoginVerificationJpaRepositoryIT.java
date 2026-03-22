package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories;

import com.issuehub.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class LoginVerificationJpaRepositoryIT {

    @Autowired
    private LoginVerificationJpaRepository repository;

    private static final String CLEAN_DB = "/db/clean/login_verifications.sql";
    private static final String DATA_DB = "/db/data/login_verifications.sql";

    @Test
    @Sql({CLEAN_DB, DATA_DB})
    void shouldInvalidatePreviousActiveCode() {
        // Given
        var loginVerificationId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        var developerId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        // When
        repository.disableActiveVerification(developerId, Instant.now());

        // Then
        var entity = repository.findById(loginVerificationId).orElseThrow();
        assertThat(entity.getUsedAt()).isNotNull();
    }

}
