package com.jgm.paladohorweb.tour.v2;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class V2Errors {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<?> response(ResponseStatusException e) {
    return ResponseEntity.status(e.getStatusCode())
        .body(
            java.util.Map.of(
                "message", e.getReason() == null ? "Solicitud rechazada" : e.getReason()));
  }

  @ExceptionHandler({
    org.springframework.dao.DataIntegrityViolationException.class,
    org.springframework.orm.ObjectOptimisticLockingFailureException.class
  })
  public ResponseEntity<?> conflict(Exception e) {
    return ResponseEntity.status(409)
        .body(
            java.util.Map.of("message", "La información cambió. Actualiza e intenta nuevamente."));
  }

  @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
  public ResponseEntity<?> auth(Exception e) {
    return ResponseEntity.status(401).body(java.util.Map.of("message", "Credenciales inválidas"));
  }
}
