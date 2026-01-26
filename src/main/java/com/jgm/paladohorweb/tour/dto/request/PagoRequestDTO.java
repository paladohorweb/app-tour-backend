
package com.jgm.paladohorweb.tour.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PagoRequestDTO(

        @NotNull
        Long reservaId,

        @Positive
        Double monto
) {}
