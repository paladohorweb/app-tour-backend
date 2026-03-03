package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.CreateReservaRequest;
import com.jgm.paladohorweb.tour.dto.request.PagoRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.PagoResponseDTO;
import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.entity.*;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.mapper.ReservaMapper;
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

    private final TourRepository tourRepository;
    private final UsuarioRepository usuarioRepository;

    private final ReservaMapper reservaMapper; // ✅ INYECTA EL MAPPER

    public ReservaResponseDTO crearReserva(CreateReservaRequest req) {

        String emailJwt = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailJwt)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Tour tour = tourRepository.findById(req.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour no encontrado"));

        if (tour.getPrecio() == null) {
            throw new ResourceNotFoundException("El tour no tiene precio configurado");
        }

        // ✅ Usa MapStruct para mapear email/nombre (lo demás lo setea el service)
        Reserva reserva = reservaMapper.toEntity(req);

        reserva.setUsuario(usuario);
        reserva.setTour(tour);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setMonto(BigDecimal.valueOf(tour.getPrecio())); // BigDecimal
        reserva.setFechaCreacion(LocalDateTime.now());

        // Defaults si no vienen en el request
        if (reserva.getEmailCliente() == null) reserva.setEmailCliente(usuario.getEmail());
        if (reserva.getNombreCliente() == null) reserva.setNombreCliente(usuario.getNombre());

        Reserva saved = reservaRepository.save(reserva);

        // ✅ DEVUELVE DTO con mapper (sin builder manual)
        return reservaMapper.toResponse(saved);
    }

    public ReservaResponseDTO obtenerReserva(Long id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        return reservaMapper.toResponse(r);
    }

    public PagoResponseDTO crearPago(PagoRequestDTO dto) throws StripeException {

        Reserva reserva = reservaRepository.findById(dto.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        Long amountMinorUnits = reserva.getMonto().longValue();

        try {
            PaymentIntent intent = stripeService.crearPaymentIntent(amountMinorUnits, reserva.getId());

            reserva.setEstado(EstadoReserva.PENDIENTE);
            reserva.setStripePaymentIntentId(intent.getId());
            reserva.setFechaCreacion(LocalDateTime.now());

            reservaRepository.save(reserva);

            return new PagoResponseDTO(intent.getClientSecret(), reserva.getId());

        } catch (StripeException e) {
            System.out.println("❌ STRIPE ERROR: " + e.getMessage());
            if (e.getStripeError() != null) {
                System.out.println("❌ STRIPE CODE: " + e.getStripeError().getCode());
                System.out.println("❌ STRIPE TYPE: " + e.getStripeError().getType());
                System.out.println("❌ STRIPE PARAM: " + e.getStripeError().getParam());
            }
            throw e;
        }
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

        reserva.setEstado(EstadoReserva.FALLIDA);
        reservaRepository.save(reserva);
    }
}
