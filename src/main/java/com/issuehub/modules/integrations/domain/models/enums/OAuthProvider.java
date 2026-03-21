package com.issuehub.modules.integrations.domain.models.enums;

public enum OAuthProvider {

    GITHUB("github"),
    GITLAB("gitlab"),;

    private final String value;

    OAuthProvider(String value) {
        this.value = value;
    }

    public static OAuthProvider fromValue(String value) {
        return OAuthProvider.valueOf(value.toUpperCase());
    }

    public String value() {
        return value;
    }

}
