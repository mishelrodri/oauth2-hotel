package com.hotel.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//si no se agrega @EnableWebSecurity, la configuración de seguridad se delega
// a las clases de autoconfiguración de Spring Boot, que proporcionan una seguridad básica por defecto.
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Este bean define CÓMO se autentica el usuario
     * Piénsalo como: "¿Quién puede pedir tokens?"
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()  // Todas las peticiones necesitan autenticación
                )
                .formLogin(Customizer.withDefaults());  // Login con formulario web simple

        return http.build();
    }

}
