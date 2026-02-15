package com.jgm.paladohorweb.tour.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservaRequest {

        @NotNull(message = "El id del tour es obligatorio")
        private Long tourId;

        @Email(message = "Email inválido")
        private String emailCliente;

        private String nombreCliente;

}