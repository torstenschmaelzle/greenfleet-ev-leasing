package io.carlease.EV_Catalogue.adapter.in.web;

import io.carlease.EV_Catalogue.domain.InvalidVehicleAttributeException;
import io.carlease.EV_Catalogue.domain.VehicleDomainException;
import io.carlease.EV_Catalogue.domain.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class EVCatalogueExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleVehicleNotFoundException(VehicleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidVehicleAttributeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidVehicleAttributeException(InvalidVehicleAttributeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(VehicleDomainException.class)
    public ResponseEntity<Map<String, String>> handleVehicleDomainException(VehicleDomainException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
