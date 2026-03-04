package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.response.TourResponseDTO;
import com.jgm.paladohorweb.tour.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tours")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TourAdminController {

    private final TourService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TourResponseDTO>> listarAdmin() {
        return ResponseEntity.ok(service.listarAdmin());
    }
}
