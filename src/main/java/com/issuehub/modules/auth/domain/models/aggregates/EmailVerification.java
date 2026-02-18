package com.issuehub.modules.auth.domain.models.aggregates;

import com.issuehub.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeAlreadyUsedException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeExpiredException;
import com.issuehub.modules.auth.domain.models.valueobjects.HashedVerificationCode;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationExpiration;
import com.issuehub.shared.domain.model.AggregateRoot;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;
import java.util.function.BiPredicate;

public class EmailVerification extends AggregateRoot {

    private final EntityId id;
    private final EntityId developerId;
    private final HashedVerificationCode hashedCode;
    private Instant usedAt;
    private final VerificationExpiration expiresAt;
    private final Instant createdAt;

    // === Factory Method ===
    public static EmailVerification create(EntityId developerId, HashedVerificationCode hashedCode, VerificationExpiration expiresAt) {
        return new EmailVerification(
                EntityId.generate(),
                developerId,
                hashedCode,
                null,
                expiresAt,
                Instant.now()
        );
    }

    // === Constructor ===
    public EmailVerification(EntityId id, EntityId developerId, HashedVerificationCode hashedCode, Instant usedAt, VerificationExpiration expiresAt, Instant createdAt) {
        this.id = id;
        this.developerId = developerId;
        this.hashedCode = hashedCode;
        this.usedAt = usedAt;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    // === Checks ===
    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isExpired(now);
    }

    // === Actions ===
    public void use(VerificationCode code, BiPredicate<String, String> verifier) {
        if (isUsed())
            throw new VerificationCodeAlreadyUsedException("Verification code has already been used");

        if (isExpired(Instant.now()))
            throw new VerificationCodeExpiredException("Verification code has expired");

        // matches
        if (!hashedCode.matches(code.value(), verifier))
            throw new InvalidVerificationCodeException("Invalid verification code");

        this.usedAt = Instant.now();
    }

    // === Getters ===
    public EntityId getId() {
        return id;
    }

    public EntityId getDeveloperId() {
        return developerId;
    }

    public HashedVerificationCode getHashedCode() {
        return hashedCode;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public VerificationExpiration getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
