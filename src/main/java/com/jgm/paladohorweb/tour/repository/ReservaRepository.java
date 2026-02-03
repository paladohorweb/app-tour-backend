package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;

public interface ReservaRepository
        extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByStripePaymentIntentId(String stripePaymentIntentId);


}


