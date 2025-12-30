package com.hotel.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http)
            throws Exception {
        http
                .authorizeExchange(auth -> auth
//                        .requestMatchers(securityProperties.getExposedAntMatchers().toArray(new String[0])).permitAll()
                        .anyExchange().authenticated()  // Todas las peticiones necesitan autenticación
                )
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(resourserver -> resourserver.jwt(Customizer.withDefaults()));

        return http.build();
    }


}
