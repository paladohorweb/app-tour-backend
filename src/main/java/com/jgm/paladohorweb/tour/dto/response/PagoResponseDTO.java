package com.jgm.paladohorweb.tour.dto.response;

public record PagoResponseDTO(
        String clientSecret,
        Long reservaId
) {}