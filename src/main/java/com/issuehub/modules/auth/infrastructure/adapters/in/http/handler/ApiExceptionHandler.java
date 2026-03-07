package com.issuehub.modules.auth.infrastructure.adapters.in.http.handler;

import com.issuehub.modules.auth.application.exceptions.AccountBlockedException;
import com.issuehub.modules.auth.application.exceptions.AccountNotFoundException;
import com.issuehub.modules.auth.application.exceptions.AccountNotVerifiedException;
import com.issuehub.modules.auth.application.exceptions.ActiveLoginCodeNotFoundException;
import com.issuehub.modules.auth.domain.exceptions.*;
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
    public ErrorResponse handleVerificationCodeExpiredException(VerificationCodeExpiredException ex, HttpServletRequest request) {
        log.warn("Verification code expired at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.GONE.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidVerificationCodeException(InvalidVerificationCodeException ex, HttpServletRequest request) {
        log.warn("Invalid verification code at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(LoginCodeAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleLoginCodeAlreadyUsedException(LoginCodeAlreadyUsedException ex, HttpServletRequest request) {
        log.warn("Login code already used at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(LoginCodeExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ErrorResponse handleLoginCodeExpiredException(LoginCodeExpiredException ex, HttpServletRequest request) {
        log.warn("Login code expired at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.GONE.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidLoginCodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidLoginCodeException(InvalidLoginCodeException ex, HttpServletRequest request) {
        log.warn("Invalid login code at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );
    }

    // === APPLICATION ===
    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAccountNotFoundException(AccountNotFoundException ex, HttpServletRequest request) {
        log.warn("Account not found at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccountBlockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccountBlockedException(AccountBlockedException ex, HttpServletRequest request) {
        log.warn("Account blocked at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccountNotVerifiedException(AccountNotVerifiedException ex, HttpServletRequest request) {
        log.warn("Account not verified at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ActiveLoginCodeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleActiveLoginCodeNotFoundException(ActiveLoginCodeNotFoundException ex, HttpServletRequest request) {
        log.warn("Active login code not found at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
    }

}
