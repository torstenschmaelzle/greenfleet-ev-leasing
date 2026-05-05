package io.carlease.fleet_consulting.adapter.in.web;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class FleetConsultingExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();
        return Map.of("error", message);
    }
}
