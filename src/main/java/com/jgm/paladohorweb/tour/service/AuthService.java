package com.jgm.paladohorweb.tour.service;


import com.jgm.paladohorweb.tour.dto.AuthResponse;
import com.jgm.paladohorweb.tour.dto.LoginRequest;
import com.jgm.paladohorweb.tour.dto.RegisterRequest;
import com.jgm.paladohorweb.tour.entity.*;
import com.jgm.paladohorweb.tour.repository.*;
import com.jgm.paladohorweb.tour.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    public AuthResponse login(LoginRequest req) {

        Usuario user = usuarioRepo.findByEmail(req.email())
                .orElseThrow();

        if (!encoder.matches(req.password(),
                user.getPassword()))
            throw new RuntimeException("Credenciales inválidas");

        String token = jwtProvider.generateToken(user.getEmail());

        return new AuthResponse(token);
    }

    public void register(RegisterRequest req) {
        Usuario u = new Usuario();
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        u.setNombre(req.nombre());
        u.setRole("TURISTA");
        usuarioRepo.save(u);
    }
}

