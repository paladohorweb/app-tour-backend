package com.jgm.paladohorweb.tour.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuiaSimpleDTO {
    private Long id;
    private String nombre;
    private String email;
}
