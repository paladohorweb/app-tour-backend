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

    public PagoResponseDTO(String clientSecret, Long id) {
    }
}