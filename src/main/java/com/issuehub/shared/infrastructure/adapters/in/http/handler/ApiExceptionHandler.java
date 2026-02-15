package com.issuehub.shared.infrastructure.adapters.in.http.handler;

import com.issuehub.shared.application.exceptions.ApplicationException;
import com.issuehub.shared.domain.exceptions.DomainException;
import com.issuehub.shared.infrastructure.adapters.in.http.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    // === DOMAIN ==
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDomainException(DomainException ex, HttpServletRequest request) {
        log.warn("Domain exception at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

    // === APPLICATION ===
    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleApplicationException(ApplicationException ex, HttpServletRequest request) {
        log.warn("Application exception at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

    // === INFRASTRUCTURE ===
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Field '%s' %s", error.getField(), error.getDefaultMessage()))
                .toList();

        log.warn("Validation failed at [{}]: {}", request.getRequestURI(), details, ex);

        return ErrorResponse.of(
                "VALIDATION_ERROR",
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST.value(),
                details,
                request.getRequestURI()
        );
    }

    // === GENERAL ===
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ErrorResponse.fromException(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
    }

}
