package com.jgm.paladohorweb.tour.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagoResponseDTO {
    private String clientSecret;
    private Long reservaId;
}
