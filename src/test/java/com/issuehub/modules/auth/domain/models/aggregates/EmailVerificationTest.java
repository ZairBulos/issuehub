package com.issuehub.modules.auth.domain.models.aggregates;

import com.issuehub.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeAlreadyUsedException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeExpiredException;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationCode;
import com.issuehub.modules.auth.domain.models.valueobjects.VerificationExpiration;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailVerificationTest {

    // === create ===
    @Test
    void shouldCreateEmailVerification() {
        // Given
        var developerId = EntityId.generate();
        var code = VerificationCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = VerificationExpiration.generate();

        // When
        var verification = EmailVerification.create(developerId, hashedCode, expiresAt);

        // Then
        assertThat(verification.isUsed()).isFalse();
        assertThat(verification.isExpired(Instant.now())).isFalse();
        assertThat(verification.getDeveloperId()).isEqualTo(developerId);
    }

    // === use ===
    @Test
    void shouldUseVerificationCode() {
        // Given
        var developerId = EntityId.generate();
        var code = VerificationCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = VerificationExpiration.generate();

        var verification = EmailVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> hash.equals("hashed-" + plain);

        // When
        verification.use(code, verifier);

        // Then
        assertThat(verification.isUsed()).isTrue();
    }

    @Test
    void shouldNotUseAlreadyUsedVerificationCode() {
        // Given
        var developerId = EntityId.generate();
        var code = VerificationCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = VerificationExpiration.generate();

        var verification = EmailVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> hash.equals("hashed-" + plain);

        verification.use(code, verifier);

        // When/Then
        assertThatThrownBy(() -> verification.use(code, verifier))
                .isInstanceOf(VerificationCodeAlreadyUsedException.class);
    }

    @Test
    void shouldNotUseExpiredVerificationCode() {
        // Given
        var developerId = EntityId.generate();
        var code = VerificationCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = new VerificationExpiration(Instant.now().minus(Duration.ofMinutes(1)));

        var verification = EmailVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> hash.equals("hashed-" + plain);

        // When/Then
        assertThatThrownBy(() -> verification.use(code, verifier))
                .isInstanceOf(VerificationCodeExpiredException.class);
    }

    @Test
    void shouldNotUseInvalidVerificationCode() {
        // Given
        var developerId = EntityId.generate();
        var code = VerificationCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = VerificationExpiration.generate();

        var verification = EmailVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> false; // always fails

        // When/Then
        assertThatThrownBy(() -> verification.use(code, verifier))
                .isInstanceOf(InvalidVerificationCodeException.class);
    }

}
