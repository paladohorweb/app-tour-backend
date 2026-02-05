
package com.jgm.paladohorweb.tour.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO{

        @NotNull
        Long reservaId;

        @Positive
        Double mont;
}
