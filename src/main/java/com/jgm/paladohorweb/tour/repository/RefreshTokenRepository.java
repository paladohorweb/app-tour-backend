package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {}
