package com.jgm.paladohorweb.tour.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponse{
        String accessToken;
        String refreshToken;
}

