package com.jgm.paladohorweb.tour.dto.response;


import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String ciudad;
    private String pais;
    private String imagenUrl;
    private Double latitud;
    private Double longitud;
    private Long precio;
}
