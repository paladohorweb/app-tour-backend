package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.PagoRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.PagoResponseDTO;
import com.jgm.paladohorweb.tour.entity.EstadoReserva;
import com.jgm.paladohorweb.tour.entity.Reserva;
import com.jgm.paladohorweb.tour.repository.ReservaRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository repository;
    private final StripeService stripeService;

    public PagoResponseDTO crearPago(PagoRequestDTO dto) throws StripeException {

        PaymentIntent intent = stripeService.crearPaymentIntent(dto.getMonto());

        Reserva reserva = Reserva.builder()
                .emailCliente(dto.getEmail())
                .nombreCliente(dto.getNombre())
                .tour(dto.getLugar())
                .monto(dto.getMonto())
                .estado(EstadoReserva.PENDIENTE)
                .stripePaymentIntentId(intent.getId())
                .fechaCreacion(LocalDateTime.now())
                .build();

        repository.save(reserva);

        return PagoResponseDTO.builder()
                .clientSecret(intent.getClientSecret())
                .reservaId(reserva.getId())
                .build();
    }
}
