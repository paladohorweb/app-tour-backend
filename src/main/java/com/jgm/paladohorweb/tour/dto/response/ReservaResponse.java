package com.jgm.paladohorweb.tour.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {


    Long id;
    //String tour,
    String tourNombre;
    BigDecimal monto;
    String estado;
}
