package com.jgm.paladohorweb.tour.dto.request;

import com.jgm.paladohorweb.tour.entity.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        @jakarta.validation.constraints.Size(min=12,max=72)
        String password;

        @NotBlank(message = "El nombre es obligatorio")
        String nombre;

        @NotNull
        private Rol rol;
        @jakarta.validation.constraints.AssertTrue private boolean acceptedTerms;
        @jakarta.validation.constraints.AssertTrue private boolean acceptedPrivacy;
        @jakarta.validation.constraints.Pattern(regexp="2026-09-v2") private String legalVersion;
        private boolean marketing;
}
