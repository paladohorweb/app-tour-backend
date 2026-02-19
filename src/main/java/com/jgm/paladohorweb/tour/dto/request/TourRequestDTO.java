package com.jgm.paladohorweb.tour.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourRequestDTO {




    private String nombre;
    private String descripcion;
    private String ciudad;
    private String pais;
    private String imagenUrl;
    private Double latitud;
    private Double longitud;

    @NotNull
    @Positive
    private BigDecimal precio;
}
