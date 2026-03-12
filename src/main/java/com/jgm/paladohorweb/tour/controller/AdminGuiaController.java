package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.response.GuiaSimpleDTO;
import com.jgm.paladohorweb.tour.entity.Rol;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/guias")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminGuiaController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<GuiaSimpleDTO>> listarGuias() {
        List<GuiaSimpleDTO> guias = usuarioRepository.findByRol(Rol.ROLE_GUIA)
                .stream()
                .map(g -> new GuiaSimpleDTO(g.getId(), g.getNombre(), g.getEmail()))
                .toList();

        return ResponseEntity.ok(guias);
    }
}