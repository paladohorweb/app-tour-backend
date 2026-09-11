package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.LoginRequest;
import com.jgm.paladohorweb.tour.dto.request.RegisterRequest;
import com.jgm.paladohorweb.tour.dto.response.AuthResponse;
import com.jgm.paladohorweb.tour.dto.response.RegisterResponse;
import com.jgm.paladohorweb.tour.entity.Rol;
import com.jgm.paladohorweb.tour.entity.Usuario;
import com.jgm.paladohorweb.tour.exception.BadRequestException;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import com.jgm.paladohorweb.tour.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email,
                                request.password
                        )
                );

        Usuario usuario = usuarioRepository.findByEmail(request.email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        String accessToken = jwtProvider.generateToken(
                usuario.getEmail(),
                usuario.getRol().name()
        );

        String refreshToken = null;

        return new AuthResponse(accessToken, refreshToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        request.setEmail(request.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        if (request.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) throw new BadRequestException("La contraseña excede el tamaño admitido");

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email ya registrado");
        }

        if (request.getRol() == Rol.ROLE_ADMIN) {
            throw new BadRequestException("No puedes registrarte como ADMIN");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setRol(request.getRol() != null ? request.getRol() : Rol.ROLE_USER); // 🔥 AQUÍ ESTABA TU PROBLEMA
        usuario.setActivo(true);
        if (!"2026-09-v2".equals(request.getLegalVersion())) throw new BadRequestException("Acepta la versión vigente de las políticas");
        usuario.setLegalVersion(request.getLegalVersion()); usuario.setConsentAt(java.time.Instant.now()); usuario.setMarketing(request.isMarketing());

        Usuario saved = usuarioRepository.save(usuario);

        return new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getNombre()
        );
    }
}
