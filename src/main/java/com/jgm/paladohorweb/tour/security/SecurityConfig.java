package com.jgm.paladohorweb.tour.security;

import com.jgm.paladohorweb.tour.security.jwt.JwtAuthEntryPoint;
import com.jgm.paladohorweb.tour.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final CustomUserDetailsService userDetailsService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, com.jgm.paladohorweb.tour.license.JgmLicenseClient license, com.fasterxml.jackson.databind.ObjectMapper licenseMapper) throws Exception {

        http
                .headers(h -> h.contentTypeOptions(Customizer.withDefaults()).frameOptions(f -> f.deny()))
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/tours").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tours/*").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v2/experiences", "/api/v2/experiences/*", "/api/v2/experiences/*/slots", "/api/v2/legal").permitAll()
                        .requestMatchers("/api/v2/**").authenticated()
                        .requestMatchers("/api/pagos/**", "/api/stripe/**").denyAll()
                        .requestMatchers(HttpMethod.POST, "/api/tours/**", "/api/reservas/**").denyAll()
                        .requestMatchers(HttpMethod.GET, "/api/reservas/**").authenticated()
                        .requestMatchers("/api/guia/**").hasRole("GUIA")

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new com.jgm.paladohorweb.tour.license.JgmLicenseGate(license, ()->"default", licenseMapper), org.springframework.security.web.access.intercept.AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

