package com.jgm.paladohorweb.tour.controller;

import com.jgm.paladohorweb.tour.dto.request.LoginRequest;
import com.jgm.paladohorweb.tour.dto.request.RegisterRequest;
import com.jgm.paladohorweb.tour.dto.response.AuthResponse;
import com.jgm.paladohorweb.tour.dto.response.RegisterResponse;
import com.jgm.paladohorweb.tour.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }
}
