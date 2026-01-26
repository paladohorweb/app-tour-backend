package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.LoginRequest;
import com.jgm.paladohorweb.tour.dto.request.RegisterRequest;
import com.jgm.paladohorweb.tour.dto.response.AuthResponse;
import com.jgm.paladohorweb.tour.dto.response.RegisterResponse;
import com.jgm.paladohorweb.tour.entity.Usuario;
import com.jgm.paladohorweb.tour.exception.BadRequestException;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import com.jgm.paladohorweb.tour.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        String accessToken = jwtProvider.generateToken(usuario.getEmail());
        String refreshToken = refreshTokenService
                .createRefreshToken(usuario.getId())
                .getToken();

        return new AuthResponse(accessToken, refreshToken);
    }

    public RegisterResponse register(RegisterRequest request) {

        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setActivo(true);

        Usuario saved = usuarioRepository.save(usuario);

        return new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getNombre()
        );
    }
}
