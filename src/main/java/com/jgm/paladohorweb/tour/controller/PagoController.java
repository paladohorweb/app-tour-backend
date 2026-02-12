package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.request.PagoRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.PagoResponseDTO;
import com.jgm.paladohorweb.tour.service.ReservaService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final ReservaService reservaService;

    @PostMapping("/crear-intent")
    public ResponseEntity<PagoResponseDTO> crearPago(
            @Valid @RequestBody PagoRequestDTO dto
    ) throws StripeException {
        return ResponseEntity.ok(reservaService.crearPago(dto));
    }
}
