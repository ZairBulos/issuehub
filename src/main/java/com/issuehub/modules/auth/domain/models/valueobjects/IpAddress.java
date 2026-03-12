package com.issuehub.modules.auth.domain.models.valueobjects;

import com.issuehub.modules.auth.domain.exceptions.InvalidIpAddressException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public record IpAddress(String value) {

    public IpAddress {
        Objects.requireNonNull(value, "IP address cannot be null");

        if (value.isBlank())
            throw new InvalidIpAddressException("IP address cannot be blank");
        if (!isValidIp(value))
            throw new InvalidIpAddressException("Invalid IP address");
    }

    private static boolean isValidIp(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

}
