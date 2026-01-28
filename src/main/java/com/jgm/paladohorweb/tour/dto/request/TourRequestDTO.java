package com.jgm.paladohorweb.tour.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourRequestDTO {


    @NotBlank
    private String nombre;
    private String descripcion;
    private String ciudad;
    private String pais;
    private String imagenUrl;
    private Double latitud;
    private Double longitud;
    private Double precio;
}
