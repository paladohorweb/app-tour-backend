package com.jgm.paladohorweb.tour.service;


import com.jgm.paladohorweb.tour.dto.response.AuthResponse;
import com.jgm.paladohorweb.tour.dto.request.LoginRequest;
import com.jgm.paladohorweb.tour.dto.request.RegisterRequest;
import com.jgm.paladohorweb.tour.entity.*;
import com.jgm.paladohorweb.tour.repository.*;
import com.jgm.paladohorweb.tour.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    public AuthResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("Usuario no existe"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }

        String accessToken = jwtProvider.generateToken(usuario.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
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

