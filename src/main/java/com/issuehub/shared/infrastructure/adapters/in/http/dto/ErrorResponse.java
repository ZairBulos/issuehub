package com.issuehub.shared.infrastructure.adapters.in.http.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String error,
        String message,
        Integer status,
        List<String> details,
        String timestamp,
        String path
) {

    public static ErrorResponse of(String error, String message, Integer status, String path) {
        return new ErrorResponse(
                error,
                message,
                status,
                null,
                Instant.now().toString(),
                path
        );
    }

    public static ErrorResponse of(String error, String message, Integer status, List<String> details, String path) {
        return new ErrorResponse(
                error,
                message,
                status,
                details,
                Instant.now().toString(),
                path
        );
    }

    public static ErrorResponse fromException(Exception ex, int status, String path) {
        return new ErrorResponse(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                status,
                null,
                Instant.now().toString(),
                path
        );
    }

    public static ErrorResponse fromException(Exception ex, int status, List<String> details, String path) {
        return new ErrorResponse(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                status,
                details,
                Instant.now().toString(),
                path
        );
    }

}
