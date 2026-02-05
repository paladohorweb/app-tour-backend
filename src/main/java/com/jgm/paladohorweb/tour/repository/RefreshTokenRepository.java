package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.RefreshToken;
import com.jgm.paladohorweb.tour.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUsuario(Usuario usuario);

    @Modifying
    @Transactional
    void deleteByUsuario(Usuario usuario);
}
