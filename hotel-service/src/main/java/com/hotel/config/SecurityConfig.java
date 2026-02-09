package com.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

     @Bean
     public JwtAuthenticationConverter jwtAuthenticationConverter() {
         JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

         converter.setPrincipalClaimName("user_name");

         return converter;
     }
}
