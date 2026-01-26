package com.jgm.paladohorweb.tour.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Email(message = "Email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
