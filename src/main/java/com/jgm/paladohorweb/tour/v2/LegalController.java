package com.jgm.paladohorweb.tour.v2;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class LegalController {
  @Value("${app.legal.name:PENDIENTE}")
  String name;

  @Value("${app.legal.tax-id:PENDIENTE}")
  String taxId;

  @Value("${app.legal.address:PENDIENTE}")
  String address;

  @Value("${app.legal.email:PENDIENTE}")
  String email;

  @GetMapping("/api/v2/legal")
  public Object legal() {
    return Map.of(
        "name",
        name,
        "taxId",
        taxId,
        "address",
        address,
        "email",
        email,
        "version",
        "2026-09-v2",
        "configured",
        !name.equals("PENDIENTE")
            && !taxId.equals("PENDIENTE")
            && !address.equals("PENDIENTE")
            && !email.equals("PENDIENTE"));
  }
}
