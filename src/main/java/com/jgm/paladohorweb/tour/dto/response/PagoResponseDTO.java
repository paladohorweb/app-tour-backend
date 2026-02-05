package com.jgm.paladohorweb.tour.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    String clientSecret;
    Long reservaId;
}