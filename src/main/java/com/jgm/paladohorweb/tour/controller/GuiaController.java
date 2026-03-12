package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import com.jgm.paladohorweb.tour.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guia")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GUIA')")
@CrossOrigin(origins = "http://localhost:4200")
public class GuiaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaResponseDTO>> misReservas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Long guiaId = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Guía no encontrado"))
                .getId();

        return ResponseEntity.ok(reservaService.reservasGuia(guiaId));
    }

    @PatchMapping("/reservas/{id}/iniciar")
    public ResponseEntity<Void> iniciar(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        reservaService.iniciarTour(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reservas/{id}/finalizar")
    public ResponseEntity<Void> finalizar(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        reservaService.finalizarTour(id, email);
        return ResponseEntity.noContent().build();
    }
}