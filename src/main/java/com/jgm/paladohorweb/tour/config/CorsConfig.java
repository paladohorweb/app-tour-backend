package com.jgm.paladohorweb.tour.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // Angular dev
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Métodos (incluye PATCH)
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

        // Headers IMPORTANTES (no uses "*" con credentials)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // Si usas cookies (hoy no, pero ok)
        config.setAllowCredentials(true);

        // Opcional: exponer headers
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
