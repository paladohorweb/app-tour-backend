package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.entity.EstadoReserva;
import com.jgm.paladohorweb.tour.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listar(
            @RequestParam(required = false) EstadoReserva estado
    ) {
        return ResponseEntity.ok(reservaService.listarAdmin(estado));
    }

    @PatchMapping("/{id}/asignar-guia/{guiaId}")
    public ResponseEntity<Void> asignarGuia(
            @PathVariable Long id,
            @PathVariable Long guiaId
    ) {
        reservaService.asignarGuia(id, guiaId);
        return ResponseEntity.noContent().build();
    }
}