package com.jgm.paladohorweb.tour.controller;


import com.jgm.paladohorweb.tour.dto.LugarRequestDTO;
import com.jgm.paladohorweb.tour.dto.LugarResponseDTO;
import com.jgm.paladohorweb.tour.service.LugarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lugares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LugarController {

    private final LugarService service;

    @GetMapping
    public ResponseEntity<List<LugarResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LugarResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<LugarResponseDTO> crear(
            @Valid @RequestBody LugarRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(dto));
    }
}
