package com.jgm.paladohorweb.tour.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TourRequestDTO {


    @NotBlank
    private String nombre;

    private String descripcion;
    private String ciudad;
    private String pais;
    private String imagenUrl;
    private Double latitud;
    private Double longitud;
}
