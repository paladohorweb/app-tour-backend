package com.jgm.paladohorweb.tour.dto.response;

import com.jgm.paladohorweb.tour.entity.EstadoReserva;
import com.jgm.paladohorweb.tour.entity.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {


    private Long id;
    private Long tourId;
    private String tourNombre;

    private Long usuarioId;
    private String emailCliente;
    private String nombreCliente;

    private BigDecimal monto;
    private EstadoReserva estado;

    private MetodoPago metodoPago;

    private String stripePaymentIntentId;
    private LocalDateTime fechaCreacion;

    private Long guiaId;
    private String guiaNombre;
    private String guiaEmail;
}
