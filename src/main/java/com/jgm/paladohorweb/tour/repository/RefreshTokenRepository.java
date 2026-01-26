package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.RefreshToken;
import com.jgm.paladohorweb.tour.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}
