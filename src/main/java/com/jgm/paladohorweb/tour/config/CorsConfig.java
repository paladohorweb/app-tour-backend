package com.jgm.paladohorweb.tour.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class CorsConfig {
 @org.springframework.beans.factory.annotation.Value("${app.cors-origins:http://localhost:4200}") private String origins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // Angular dev
        config.setAllowedOrigins(java.util.Arrays.asList(origins.split(",")));

        // Métodos (incluye PATCH)
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

        // Headers IMPORTANTES (no uses "*" con credentials)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // Si usas cookies (hoy no, pero ok)
        config.setAllowCredentials(false);

        // Opcional: exponer headers
        config.setExposedHeaders(List.of("Authorization", "X-JGM-License-State", "X-JGM-Billing-Url"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

