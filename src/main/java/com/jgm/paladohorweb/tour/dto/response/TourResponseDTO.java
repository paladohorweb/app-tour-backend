package com.jgm.paladohorweb.tour.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDTO {

    private String nombre;
    private String descripcion;
    private String ciudad;
    private String pais;
    private String imagenUrl;
    private Double latitud;
    private Double longitud;
    private Double precio;
}
