package com.issuehub.modules.integrations.infrastructure.adapters.security;

import com.issuehub.modules.integrations.application.exceptions.EncryptionException;
import com.issuehub.modules.integrations.infrastructure.config.EncryptionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AesEncryptionAdapterTest {

    private AesEncryptionAdapter aesEncryptionAdapter;

    @BeforeEach
    void setup() {
        var key = new byte[32];
        new SecureRandom().nextBytes(key);
        var properties = new EncryptionProperties(Base64.getEncoder().encodeToString(key));
        aesEncryptionAdapter = new AesEncryptionAdapter(properties);
    }

    @Test
    void shouldEncryptAndDecryptValue() {
        // Given
        var plain = "ghu_accesstoken123";

        // When
        var encrypted = aesEncryptionAdapter.encrypt(plain);
        var decrypted = aesEncryptionAdapter.decrypt(encrypted);

        // Then
        assertThat(decrypted).isEqualTo(plain);
        assertThat(encrypted).isNotEqualTo(plain);
    }

    @Test
    void shouldProduceDifferentCiphertextEachTime() {
        // Given
        var plain = "ghu_accesstoken123";

        // When
        var first  = aesEncryptionAdapter.encrypt(plain);
        var second = aesEncryptionAdapter.encrypt(plain);

        // Then
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldThrowEncryptionException_whenKeyIsInvalid() {
        // Given
        var invalidProperties = new EncryptionProperties("bm90YXZhbGlka2V5");
        var adapter = new AesEncryptionAdapter(invalidProperties);

        // When/Then
        assertThatThrownBy(() -> adapter.encrypt("value"))
                .isInstanceOf(EncryptionException.class);
    }

    @Test
    void shouldThrowEncryptionException_whenDecryptingInvalidData() {
        // When/Then
        assertThatThrownBy(() -> aesEncryptionAdapter.decrypt("not-valid-base64-encrypted-data"))
                .isInstanceOf(EncryptionException.class);
    }

}
