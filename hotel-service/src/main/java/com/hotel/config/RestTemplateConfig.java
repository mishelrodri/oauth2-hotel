package com.hotel.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final JwtPropagationInterceptor jwtPropagationInterceptor;


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.interceptors(jwtPropagationInterceptor).build();
    }

}
