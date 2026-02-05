package com.jgm.paladohorweb.tour.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

        @Email(message = "Email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email;

        @NotBlank(message = "La contraseña es obligatoria")
        String password;

        @NotBlank(message = "El nombre es obligatorio")
        String nombre;
}
