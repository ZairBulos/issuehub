package com.issuehub.modules.auth.domain.models.aggregates;

import com.issuehub.modules.auth.domain.exceptions.InvalidLoginCodeException;
import com.issuehub.modules.auth.domain.exceptions.LoginCodeAlreadyUsedException;
import com.issuehub.modules.auth.domain.exceptions.LoginCodeExpiredException;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCode;
import com.issuehub.modules.auth.domain.models.valueobjects.LoginCodeExpiration;
import com.issuehub.shared.domain.model.EntityId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginVerificationTest {

    // === create ===
    @Test
    void shouldCreateLoginVerification() {
        // Given
        var developerId = EntityId.generate();
        var code = LoginCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = LoginCodeExpiration.generate();

        // When
        var verification = LoginVerification.create(developerId, hashedCode, expiresAt);

        // Then
        assertThat(verification.isUsed()).isFalse();
        assertThat(verification.isExpired(Instant.now())).isFalse();
        assertThat(verification.getDeveloperId()).isEqualTo(developerId);
    }

    // === use ===
    @Test
    void shouldUseLoginCode() {
        // Given
        var developerId = EntityId.generate();
        var code = LoginCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = LoginCodeExpiration.generate();

        var verification = LoginVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> hash.equals("hashed-" + plain);

        // When
        verification.use(code, verifier);

        // Then
        assertThat(verification.isUsed()).isTrue();
    }

    @Test
    void shouldNotUseAlreadyUsedLoginCode() {
        // Given
        var developerId = EntityId.generate();
        var code = LoginCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = LoginCodeExpiration.generate();

        var verification = LoginVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> hash.equals("hashed-" + plain);

        verification.use(code, verifier);

        // When/Then
        assertThatThrownBy(() -> verification.use(code, verifier))
                .isInstanceOf(LoginCodeAlreadyUsedException.class);
    }

    @Test
    void shouldNotUseExpiredLoginCode() {
        // Given
        var developerId = EntityId.generate();
        var code = LoginCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = new LoginCodeExpiration(Instant.now().minus(Duration.ofMinutes(1)));

        var verification = LoginVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> hash.equals("hashed-" + plain);

        // When/Then
        assertThatThrownBy(() -> verification.use(code, verifier))
                .isInstanceOf(LoginCodeExpiredException.class);
    }

    @Test
    void shouldNotUseInvalidLoginCode() {
        // Given
        var developerId = EntityId.generate();
        var code = LoginCode.generate();
        var hashedCode = code.toHashed(plain -> "hashed-" + plain);
        var expiresAt = LoginCodeExpiration.generate();

        var verification = LoginVerification.create(developerId, hashedCode, expiresAt);

        BiPredicate<String, String> verifier = (plain, hash) -> false; // always fails

        // When/Then
        assertThatThrownBy(() -> verification.use(code, verifier))
                .isInstanceOf(InvalidLoginCodeException.class);
    }

}
