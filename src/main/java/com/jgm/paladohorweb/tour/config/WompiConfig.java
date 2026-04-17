package com.jgm.paladohorweb.tour.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WompiConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}