package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.entity.RefreshToken;
import com.jgm.paladohorweb.tour.entity.Usuario;
import com.jgm.paladohorweb.tour.repository.RefreshTokenRepository;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirationMs;

    public RefreshToken createRefreshToken(Long userId) {

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("Usuario no encontrado"));

        RefreshToken token = new RefreshToken();
        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        return repository.save(token);
    }

    public RefreshToken verifyExpiration(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token inválido"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(refreshToken);
            throw new BusinessException("Refresh token expirado");
        }

        return refreshToken;
    }

    public void deleteByUser(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("Usuario no encontrado"));

        repository.deleteByUsuario(usuario);
    }
}


