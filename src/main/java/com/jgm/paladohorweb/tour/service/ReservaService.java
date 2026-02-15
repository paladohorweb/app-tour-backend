package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.CreateReservaRequest;
import com.jgm.paladohorweb.tour.dto.response.PagoRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.entity.EstadoReserva;
import com.jgm.paladohorweb.tour.entity.Reserva;
import com.jgm.paladohorweb.tour.entity.Tour;
import com.jgm.paladohorweb.tour.entity.Usuario;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.repository.ReservaRepository;
import com.jgm.paladohorweb.tour.repository.TourRepository;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // ✅ nuevos
    private final TourRepository tourRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaResponseDTO crearReserva(CreateReservaRequest req) {

        // Usuario autenticado (email viene en el JWT subject)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Tour tour = tourRepository.findById(req.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour no encontrado"));

        // monto viene del tour (fuente de verdad)
        BigDecimal monto = BigDecimal.valueOf(tour.getPrecio()); // BigDecimal

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .tour(tour)
                .estado(EstadoReserva.PENDIENTE)
                .monto(monto)
                .emailCliente(req.getEmailCliente() != null ? req.getEmailCliente() : usuario.getEmail())
                .nombreCliente(req.getNombreCliente() != null ? req.getNombreCliente() : usuario.getNombre())
                .fechaCreacion(LocalDateTime.now())
                .build();

        Reserva saved = reservaRepository.save(reserva);

        return toResponse(saved);
    }

    public ReservaResponseDTO obtenerReserva(Long id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        return toResponse(r);
    }

    public PagoRequestDTO crearPago(com.jgm.paladohorweb.tour.dto.request.PagoRequestDTO dto) throws StripeException {

        Reserva reserva = reservaRepository.findById(dto.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // ✅ seguridad: el monto NO debe venir del frontend (evitas manipulación)
        // Usamos el monto real guardado en DB
        Long amountMinorUnits = reserva.getMonto().longValue();

        PaymentIntent intent = stripeService.crearPaymentIntent(amountMinorUnits, reserva.getId());

        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setStripePaymentIntentId(intent.getId());

        reservaRepository.save(reserva);

        return new PagoRequestDTO();
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

    private ReservaResponseDTO toResponse(Reserva r) {
        return ReservaResponseDTO.builder()
                .id(r.getId())
                .tourId(r.getTour().getId())
                .tourNombre(r.getTour().getNombre())
                .usuarioId(r.getUsuario().getId())
                .emailCliente(r.getEmailCliente())
                .nombreCliente(r.getNombreCliente())
                .monto(r.getMonto())
                .estado(r.getEstado())
                .stripePaymentIntentId(r.getStripePaymentIntentId())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }






}


