package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.PagoRequestDTO;
import com.jgm.paladohorweb.tour.dto.PagoResponseDTO;
import com.jgm.paladohorweb.tour.service.ReservaService;
import com.stripe.exception.StripeException;
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
            @RequestBody PagoRequestDTO dto
    ) throws StripeException {
        return ResponseEntity.ok(reservaService.crearPago(dto));
    }
}
