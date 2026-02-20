package com.issuehub.modules.auth.infrastructure.adapters.in.http.handler;

import com.issuehub.modules.auth.domain.exceptions.InvalidVerificationCodeException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeAlreadyUsedException;
import com.issuehub.modules.auth.domain.exceptions.VerificationCodeExpiredException;
import com.issuehub.shared.infrastructure.adapters.in.http.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Component("authApiExceptionHandler")
@RestControllerAdvice(basePackages = "com.issuehub.modules.auth")
public class ApiExceptionHandler {

    // === DOMAIN ===
    @ExceptionHandler(VerificationCodeAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleVerificationCodeAlreadyUsed(VerificationCodeAlreadyUsedException ex, HttpServletRequest request) {
        log.warn("Verification code already used at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(VerificationCodeExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ErrorResponse handleVerificationCodeAlreadyUsed(VerificationCodeExpiredException ex, HttpServletRequest request) {
        log.warn("Verification code expired at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.GONE.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVerificationCodeAlreadyUsed(InvalidVerificationCodeException ex, HttpServletRequest request) {
        log.warn("Invalid verification code at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

}
