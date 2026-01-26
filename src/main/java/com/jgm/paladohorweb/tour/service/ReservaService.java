package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.PagoRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.PagoResponseDTO;
import com.jgm.paladohorweb.tour.entity.EstadoReserva;
import com.jgm.paladohorweb.tour.entity.Reserva;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.repository.ReservaRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final StripeService stripeService;

    public PagoResponseDTO crearPago(PagoRequestDTO dto) throws StripeException {

        Reserva reserva = reservaRepository.findById(dto.reservaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reserva no encontrada"));

        PaymentIntent intent =
                stripeService.crearPaymentIntent(BigDecimal.valueOf(dto.monto()));

        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setMonto(BigDecimal.valueOf(dto.monto()));
        reserva.setStripePaymentIntentId(intent.getId());
        reserva.setFechaCreacion(LocalDateTime.now());

        reservaRepository.save(reserva);

        return new PagoResponseDTO(
                intent.getClientSecret(),
                reserva.getId()
        );
    }
}

