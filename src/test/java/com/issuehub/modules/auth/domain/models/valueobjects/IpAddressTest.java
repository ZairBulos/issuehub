package com.issuehub.modules.auth.domain.models.valueobjects;

import com.issuehub.modules.auth.domain.exceptions.InvalidIpAddressException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IpAddressTest {

    @Test
    void shouldCreateIPv4() {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                new IpAddress("192.168.1.1")
        );
    }

    @Test
    void shouldCreateIPv6() {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                new IpAddress("2001:0db8:0000:0000:0000:0000:0000:0001")
        );
    }

    @Test
    void shouldCreateIPv6Abbreviated() {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                new IpAddress("2001:db8::1")
        );
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        // When/Then
        assertThatThrownBy(() -> new IpAddress(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenBlank() {
        // When/Then
        assertThatThrownBy(() -> new IpAddress("  "))
                .isInstanceOf(InvalidIpAddressException.class);
    }

    @Test
    void shouldThrowExceptionWhenInvalid() {
        // When/Then
        assertThatThrownBy(() -> new IpAddress("999.999.999.999"))
                .isInstanceOf(InvalidIpAddressException.class);
    }

    @Test
    void shouldThrowExceptionWhenMalformed() {
        // When/Then
        assertThatThrownBy(() -> new IpAddress("not-an-ip"))
                .isInstanceOf(InvalidIpAddressException.class);
    }

}
