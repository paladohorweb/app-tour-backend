package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.entity.RefreshToken;
import com.jgm.paladohorweb.tour.entity.Usuario;
import com.jgm.paladohorweb.tour.exception.BadRequestException;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.repository.RefreshTokenRepository;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirationMs;


    @Transactional
    public RefreshToken createRefreshToken(Long userId) {

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        // 🔥 FORZAR BORRADO REAL
        repository.findByUsuario(usuario)
                .ifPresent(repository::delete);

        repository.flush(); // 👈 CLAVE


        RefreshToken token = new RefreshToken();
        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        return repository.save(token);
    }

    public RefreshToken verifyExpiration(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() ->
                        new BadRequestException("Refresh token inválido"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(refreshToken);
            throw new BadRequestException("Refresh token expirado");
        }

        return refreshToken;
    }

    public void deleteByUser(Long userId) {

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        repository.deleteByUsuario(usuario);
    }
}
