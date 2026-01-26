package com.jgm.paladohorweb.tour.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateReservaRequest(

        @NotNull(message = "El id del tour es obligatorio")
        Long tourId,

        @Email(message = "Email inválido")
        String emailCliente
) {}
