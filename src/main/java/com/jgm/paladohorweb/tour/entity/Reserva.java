package com.jgm.paladohorweb.tour.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailCliente;
    private String nombreCliente;

    private String lugar;

    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    private String stripePaymentIntentId;

    private LocalDateTime fechaCreacion;
}
