package io.carlease.fleet_consulting.adapter.in.web;

import io.carlease.fleet_consulting.domain.InvalidFleetAttributeException;
import io.carlease.fleet_consulting.domain.NoVehiclesMatchingRequirementsException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class FleetConsultingExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(FleetConsultingExceptionHandler.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().iterator().next().getMessage();
        logger.warn("Constraint violation: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }

    @ExceptionHandler(InvalidFleetAttributeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFleetAttribute(InvalidFleetAttributeException ex) {
        logger.warn("Invalid fleet attribute: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NoVehiclesMatchingRequirementsException.class)
    public ResponseEntity<Map<String, String>> handleNoVehiclesFound(NoVehiclesMatchingRequirementsException ex) {
        logger.warn("No vehicles matching requirements: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", ex.getMessage()));
    }
}
