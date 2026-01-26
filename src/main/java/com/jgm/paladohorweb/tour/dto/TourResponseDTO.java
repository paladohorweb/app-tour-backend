package com.jgm.paladohorweb.tour.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String ciudad;
    private String pais;
    private String imagenUrl;
    private Double latitud;
    private Double longitud;
}
