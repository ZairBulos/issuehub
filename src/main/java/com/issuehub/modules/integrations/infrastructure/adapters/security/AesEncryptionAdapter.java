package com.issuehub.modules.integrations.infrastructure.adapters.security;

import com.issuehub.modules.integrations.application.exceptions.EncryptionException;
import com.issuehub.modules.integrations.application.ports.security.EncryptionPort;
import com.issuehub.modules.integrations.infrastructure.config.EncryptionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AesEncryptionAdapter implements EncryptionPort {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EncryptionProperties encryptionProperties;

    @Override
    public String encrypt(String value) {
        try {
            var iv = generateIV();
            var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, buildKey(), new GCMParameterSpec(TAG_LENGTH, iv));

            var encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            var combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt value", e);
        }
    }

    @Override
    public String decrypt(String value) {
        try {
            var combined = Base64.getDecoder().decode(value);
            var iv = Arrays.copyOfRange(combined, 0, IV_LENGTH);
            var encrypted = Arrays.copyOfRange(combined, IV_LENGTH, combined.length);

            var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, buildKey(), new GCMParameterSpec(TAG_LENGTH, iv));

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt value", e);
        }
    }

    private byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    private SecretKeySpec buildKey() {
        var keyBytes = Base64.getDecoder().decode(encryptionProperties.secretKey());
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

}
