package com.jgm.paladohorweb.tour.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    @NotNull
    private String clientSecret;
    private Long reservaId;

    private String provider;          // WOMPI | MANUAL | STRIPE
    private String checkoutUrl;       // redirect URL si aplica
    private String reference;         // referencia interna

    public PagoResponseDTO(String clientSecret, Long id) {
    }
}