package com.jgm.paladohorweb.tour.controller;


import com.jgm.paladohorweb.tour.dto.request.TourRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.TourResponseDTO;
import com.jgm.paladohorweb.tour.exception.BadRequestException;
import com.jgm.paladohorweb.tour.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TourController {

    private final TourService service;

    @GetMapping
    public ResponseEntity<List<TourResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TourResponseDTO> crear(@Valid @RequestBody TourRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TourResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TourRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activo")
    public ResponseEntity<Void> cambiarActivo(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean activo = body.get("activo");
        if (activo == null) throw new BadRequestException("Campo 'activo' es obligatorio");
        service.cambiarActivo(id, activo);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }


}
