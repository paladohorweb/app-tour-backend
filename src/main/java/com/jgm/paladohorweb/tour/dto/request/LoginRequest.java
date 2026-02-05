package com.jgm.paladohorweb.tour.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

        @Email(message = "Email inválido")
        @NotBlank(message = "El email es obligatorio")
       public String email;

        @NotBlank(message = "La contraseña es obligatoria")
        public String password;
}
