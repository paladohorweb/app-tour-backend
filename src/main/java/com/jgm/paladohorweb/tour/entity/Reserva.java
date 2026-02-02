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

     /* ==========================
       RELACIONES CORRECTAS
       ========================== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;   // ✅ ENTIDAD, NO STRING

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;


    @Column(unique = true)
    private String stripePaymentIntentId;

    private LocalDateTime fechaCreacion;
}
