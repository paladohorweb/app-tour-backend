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

        Reserva reserva = reservaRepository.findById(dto.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // ✅ Stripe amount en minor units (Long)
        Long amount = dto.getMonto();

        PaymentIntent intent = stripeService.crearPaymentIntent(amount, reserva.getId());

        // Persistimos “intención” y dejamos estado pendiente
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setMonto(BigDecimal.valueOf(amount)); // si usas COP está ok, ideal: guardar moneda también
        reserva.setStripePaymentIntentId(intent.getId());
        reserva.setFechaCreacion(LocalDateTime.now());

        reservaRepository.save(reserva);

        return new PagoResponseDTO(intent.getClientSecret(), reserva.getId());
    }

    public void marcarReservaPagada(String paymentIntentId) {
        Reserva reserva = reservaRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada para paymentIntentId: " + paymentIntentId));

        reserva.setEstado(EstadoReserva.PAGADA);
        reservaRepository.save(reserva);
    }

    public void marcarReservaFallida(String paymentIntentId) {
        Reserva reserva = reservaRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada para paymentIntentId: " + paymentIntentId));

        reserva.setEstado(EstadoReserva.FALLIDA); // si no tienes FALLIDA, créala
        reservaRepository.save(reserva);
    }
}


