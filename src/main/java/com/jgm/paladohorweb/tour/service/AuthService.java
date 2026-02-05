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

        String refreshToken = refreshTokenService
                .createRefreshToken(usuario.getId())
                .getToken();

        return new AuthResponse(accessToken, refreshToken);
    }

    public RegisterResponse register(RegisterRequest request) {

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
         usuario.setRol(Rol.ROLE_USER); // 🔥 AQUÍ ESTABA TU PROBLEMA
        usuario.setActivo(true);

        Usuario saved = usuarioRepository.save(usuario);

        return new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getNombre()
        );
    }
}
