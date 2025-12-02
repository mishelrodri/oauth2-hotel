package com.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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

    /**
     * Este bean define QUÉ usuarios existen en el sistema
     * En producción esto vendría de una base de datos
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}password")  // {noop} significa "sin encriptar" (solo para desarrollo)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

}
