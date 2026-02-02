package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;

public interface ReservaRepository
        extends JpaRepository<Reserva, Long> {
    ScopedValue<Object> findByPaymentIntentId(String paymentIntentId);
}

