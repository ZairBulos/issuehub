package com.issuehub.modules.auth.infrastructure.adapters.out.persistence.repositories;

import com.issuehub.modules.auth.infrastructure.adapters.out.persistence.entities.LoginVerificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface LoginVerificationJpaRepository extends JpaRepository<LoginVerificationJpaEntity, UUID> {

    @Modifying
    @Query(
            """
            UPDATE LoginVerificationJpaEntity v
            SET v.usedAt = :now
            WHERE v.developerId = :developerId AND v.usedAt IS NULL
            """
    )
    void disableActiveVerification(@Param("developerId") UUID developerId, @Param("now") Instant now);

}
