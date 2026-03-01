package com.issuehub.modules.auth.domain.models.valueobjects;

import com.issuehub.modules.auth.domain.exceptions.InvalidIpAddressException;

import java.util.Objects;
import java.util.regex.Pattern;

public record IpAddress(String value) {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
    );
    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
            + "|^(([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?::(([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})?$"
    );

    public IpAddress {
        Objects.requireNonNull(value, "IP address cannot be null");

        if (value.isBlank())
            throw new InvalidIpAddressException("IP address cannot be blank");
        if (!IPV4_PATTERN.matcher(value).matches() && !IPV6_PATTERN.matcher(value).matches())
            throw new InvalidIpAddressException("Invalid IP address");
    }

}
