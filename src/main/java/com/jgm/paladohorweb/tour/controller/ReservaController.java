package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.request.CreateReservaRequest;
import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody CreateReservaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crearReserva(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerReserva(id));
    }
}

