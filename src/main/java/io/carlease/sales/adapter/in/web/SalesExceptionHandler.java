package io.carlease.sales.adapter.in.web;

import io.carlease.sales.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class SalesExceptionHandler extends ResponseEntityExceptionHandler {

    // --- Bean Validation failures (@Valid on @RequestBody) ---

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    // --- Domain: invalid input (400) ---

    @ExceptionHandler({InvalidCustomerAttributeException.class, InvalidFleetAttributeException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(ContractDomainException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    // --- Domain: not found (404) ---

    @ExceptionHandler(ContractNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ContractDomainException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // --- Domain: already signed (409 Conflict) ---

    @ExceptionHandler(ContractAlreadySignedException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ContractAlreadySignedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    // --- Domain: reservation expired (409 Conflict) ---

    @ExceptionHandler(ReservationExpiredException.class)
    public ResponseEntity<Map<String, String>> handleReservationExpired(ReservationExpiredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

}
