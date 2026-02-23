package com.issuehub.modules.auth.domain.models.aggregates;

import com.issuehub.modules.auth.domain.exceptions.InvalidLoginCodeException;
import com.issuehub.modules.auth.domain.exceptions.LoginCodeAlreadyUsedException;
import com.issuehub.modules.auth.domain.exceptions.LoginCodeExpiredException;
import com.issuehub.modules.auth.domain.models.valueobjects.HashedLoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCodeExpiration;
import com.issuehub.shared.domain.model.AggregateRoot;
import com.issuehub.shared.domain.model.EntityId;

import java.time.Instant;
import java.util.function.BiPredicate;

public class LoginVerification extends AggregateRoot {

    private final EntityId id;
    private final EntityId developerId;
    private final HashedLoginCode hashedCode;
    private Instant usedAt;
    private final LoginCodeExpiration expiresAt;
    private final Instant createdAt;

    // === Factory Method ===
    public static LoginVerification create(EntityId developerId, HashedLoginCode hashedCode, LoginCodeExpiration expiresAt) {
        return new LoginVerification(
                EntityId.generate(),
                developerId,
                hashedCode,
                null,
                expiresAt,
                Instant.now()
        );
    }

    // === Constructor ===
    public LoginVerification(EntityId id, EntityId developerId, HashedLoginCode hashedCode, Instant usedAt, LoginCodeExpiration expiresAt, Instant createdAt) {
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
    public void use(LoginCode code, BiPredicate<String, String> verifier) {
        if (isUsed())
            throw new LoginCodeAlreadyUsedException("Login code has already been used");

        if (isExpired(Instant.now()))
            throw new LoginCodeExpiredException("Login code has expired");

        if (!hashedCode.matches(code.value(), verifier))
            throw new InvalidLoginCodeException("Invalid login code");

        this.usedAt = Instant.now();
    }

    // === Getters ===
    public EntityId getId() {
        return id;
    }

    public EntityId getDeveloperId() {
        return developerId;
    }

    public HashedLoginCode getHashedCode() {
        return hashedCode;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public LoginCodeExpiration getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
