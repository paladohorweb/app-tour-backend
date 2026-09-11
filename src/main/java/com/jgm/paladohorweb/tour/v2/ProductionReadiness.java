package com.jgm.paladohorweb.tour.v2;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProductionReadiness {
  @Value("${app.legal.name:PENDIENTE}")
  String name;

  @Value("${app.legal.tax-id:PENDIENTE}")
  String taxId;

  @Value("${app.legal.address:PENDIENTE}")
  String address;

  @Value("${app.legal.email:PENDIENTE}")
  String email;

  @Value("${app.cors-origins}")
  String origins;

  @Value("${app.legal.approved:false}")
  boolean approved;

  @PostConstruct
  void check() {
    if (!approved
        || java.util.stream.Stream.of(name, taxId, address, email)
            .anyMatch(s -> s.isBlank() || s.contains("PENDIENTE"))
        || !email.contains("@")
        || java.util.Arrays.stream(origins.split(","))
            .anyMatch(s -> !s.startsWith("https://") || s.contains("*")))
      throw new IllegalStateException(
          "Completa LEGAL_* y CORS_ALLOWED_ORIGINS HTTPS antes de iniciar producción");
  }
}
