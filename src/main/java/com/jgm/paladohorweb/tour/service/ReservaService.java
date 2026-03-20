package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.CreateReservaRequest;
import com.jgm.paladohorweb.tour.dto.request.PagoRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.PagoResponseDTO;
import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.entity.*;
import com.jgm.paladohorweb.tour.exception.BadRequestException;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.mapper.ReservaMapper;
import com.jgm.paladohorweb.tour.repository.ReservaRepository;
import com.jgm.paladohorweb.tour.repository.TourRepository;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final StripeService stripeService;
    private final TourRepository tourRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaMapper reservaMapper;

    public ReservaResponseDTO crearReserva(CreateReservaRequest req) {

        String emailJwt = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailJwt)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Tour tour = tourRepository.findById(req.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour no encontrado"));

        if (tour.getPrecio() == null) {
            throw new ResourceNotFoundException("El tour no tiene precio configurado");
        }

        Reserva reserva = reservaMapper.toEntity(req);

        reserva.setUsuario(usuario);
        reserva.setTour(tour);
        reserva.setGuia(null);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        // Si tu Tour.precio es BigDecimal, deja esto:
        reserva.setMonto(BigDecimal.valueOf(tour.getPrecio()));

        // Si Tour.precio fuera Double, usar:
        // reserva.setMonto(BigDecimal.valueOf(tour.getPrecio()));

        reserva.setFechaCreacion(LocalDateTime.now());

        if (reserva.getEmailCliente() == null || reserva.getEmailCliente().isBlank()) {
            reserva.setEmailCliente(usuario.getEmail());
        }

        if (reserva.getNombreCliente() == null || reserva.getNombreCliente().isBlank()) {
            reserva.setNombreCliente(usuario.getNombre());
        }

        Reserva saved = reservaRepository.save(reserva);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerReserva(Long id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        return toResponse(r);
    }

    public PagoResponseDTO crearPago(PagoRequestDTO dto) throws StripeException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Reserva reserva = reservaRepository.findById(dto.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        boolean esDueno = reserva.getUsuario() != null
                && reserva.getUsuario().getEmail().equals(email);

        boolean esAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esDueno && !esAdmin) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }

        if (reserva.getEstado() == EstadoReserva.PAGADA) {
            throw new BadRequestException("La reserva ya fue pagada");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BadRequestException("No puedes pagar una reserva cancelada");
        }

        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new BadRequestException("No puedes pagar una reserva finalizada");
        }

        if (reserva.getMonto() == null) {
            throw new BadRequestException("La reserva no tiene monto configurado");
        }

        reserva.setMetodoPago(dto.getMetodoPago());

        // Si el método es tarjeta, usa Stripe
        if (dto.getMetodoPago() == MetodoPago.TARJETA) {
            Long amountMinorUnits = reserva.getMonto().longValue();

            PaymentIntent intent = stripeService.crearPaymentIntent(amountMinorUnits, reserva.getId());

            reserva.setStripePaymentIntentId(intent.getId());
            reservaRepository.save(reserva);

            return new PagoResponseDTO(intent.getClientSecret(), reserva.getId());
        }

        // Para métodos de prueba/manuales, marcamos como pagada directamente
        // Esto te sirve mientras montas otras pasarelas
        reserva.setEstado(EstadoReserva.PAGADA);
        reservaRepository.save(reserva);

        return new PagoResponseDTO("MANUAL_OK", reserva.getId());
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

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarMisReservas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return reservaRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void cancelarMiReserva(Long reservaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reserva.getUsuario().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }

        if (reserva.getEstado() == EstadoReserva.PAGADA
                || reserva.getEstado() == EstadoReserva.EN_CURSO
                || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new BadRequestException("No puedes cancelar una reserva pagada/en curso/finalizada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarAdmin(EstadoReserva estado) {

        List<Reserva> reservas = (estado == null)
                ? reservaRepository.findAllByOrderByFechaCreacionDesc()
                : reservaRepository.findByEstadoOrderByFechaCreacionDesc(estado);

        return reservas.stream()
                .map(this::toResponse)
                .toList();
    }

    public void asignarGuia(Long reservaId, Long guiaId) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        Usuario guia = usuarioRepository.findById(guiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Guía no encontrado"));

        if (guia.getRol() != Rol.ROLE_GUIA) {
            throw new BadRequestException("El usuario seleccionado no es guía");
        }

        if (reserva.getEstado() != EstadoReserva.PAGADA) {
            throw new BadRequestException("Solo se puede asignar guía a una reserva pagada");
        }

        reserva.setGuia(guia);
        reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> reservasGuia(Long guiaId) {

        return reservaRepository.findByGuiaIdOrderByFechaCreacionDesc(guiaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void iniciarTour(Long reservaId, String emailGuia) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (reserva.getGuia() == null || !reserva.getGuia().getEmail().equals(emailGuia)) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }

        if (reserva.getEstado() != EstadoReserva.PAGADA) {
            throw new BadRequestException("La reserva debe estar pagada para iniciar el tour");
        }

        reserva.setEstado(EstadoReserva.EN_CURSO);
        reservaRepository.save(reserva);
    }

    public void finalizarTour(Long reservaId, String emailGuia) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (reserva.getGuia() == null || !reserva.getGuia().getEmail().equals(emailGuia)) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }

        if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new BadRequestException("El tour no está en curso");
        }

        reserva.setEstado(EstadoReserva.FINALIZADA);
        reservaRepository.save(reserva);
    }

    private ReservaResponseDTO toResponse(Reserva r) {
        return ReservaResponseDTO.builder()
                .id(r.getId())
                .tourId(r.getTour() != null ? r.getTour().getId() : null)
                .tourNombre(r.getTour() != null ? r.getTour().getNombre() : null)
                .usuarioId(r.getUsuario() != null ? r.getUsuario().getId() : null)
                .emailCliente(r.getEmailCliente())
                .nombreCliente(r.getNombreCliente())
                .monto(r.getMonto())
                .estado(r.getEstado())
                .metodoPago(r.getMetodoPago())
                .stripePaymentIntentId(r.getStripePaymentIntentId())
                .fechaCreacion(r.getFechaCreacion())
                .guiaId(r.getGuia() != null ? r.getGuia().getId() : null)
                .guiaNombre(r.getGuia() != null ? r.getGuia().getNombre() : null)
                .guiaEmail(r.getGuia() != null ? r.getGuia().getEmail() : null)
                .build();
    }

    public void eliminarMiReservaSiNoFuePagada(Long reservaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        boolean esDueno = reserva.getUsuario() != null
                && reserva.getUsuario().getEmail().equals(email);

        boolean esAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esDueno && !esAdmin) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }

        if (reserva.getEstado() == EstadoReserva.PAGADA
                || reserva.getEstado() == EstadoReserva.EN_CURSO
                || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new BadRequestException("No se puede eliminar una reserva pagada, en curso o finalizada");
        }

        reservaRepository.delete(reserva);
    }


}