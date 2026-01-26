package com.jgm.paladohorweb.tour.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequestDTO {
    private String email;
    private String nombre;
    private String lugar;
    private BigDecimal monto;
}
