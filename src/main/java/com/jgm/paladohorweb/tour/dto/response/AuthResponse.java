package com.jgm.paladohorweb.tour.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}

