package com.issuehub.modules.developers.domain.models.valueobjects;

import com.issuehub.modules.developers.domain.exceptions.InvalidDeveloperEmailException;

import java.util.Objects;
import java.util.regex.Pattern;

public record DeveloperEmail(String value) {

    private static final int EMAIL_MAX_LENGTH = 320;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public DeveloperEmail {
        Objects.requireNonNull(value, "Developer email cannot be null");

        value = value.trim().toLowerCase();

        if (value.length() > EMAIL_MAX_LENGTH)
            throw new InvalidDeveloperEmailException("Developer email length exceeds maximum length");
        if (!EMAIL_PATTERN.matcher(value).matches())
            throw new InvalidDeveloperEmailException("Invalid email address");
    }

}
