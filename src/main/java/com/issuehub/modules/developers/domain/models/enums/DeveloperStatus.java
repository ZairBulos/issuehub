package com.issuehub.modules.developers.domain.models.enums;

public enum DeveloperStatus {

    ACTIVE("active"),
    BLOCKED("blocked"),
    DELETED("deleted");

    private final String value;

    DeveloperStatus(String value) {
        this.value = value;
    }

    public static DeveloperStatus fromValue(String value) {
        return DeveloperStatus.valueOf(value.toUpperCase());
    }

    public String value() {
        return value;
    }

}
