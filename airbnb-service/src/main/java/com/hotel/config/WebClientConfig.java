package com.hotel.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    /**
     * Esta solucion incluye el filtro, esto nos ahorra estar repitiendo codigo PERO
     * crea una NUEVA REQUEST, por lo que al final esto consume memoria (poca pero consume)
     * entonces dejamos mejor el Bean normal sin el filtro y yan manualmente nosotros agrgamos el jwt en cada request xd
     * Igualsegun lo que dijo Hernan era mejor aveces dejarlo asi porque hay gente que no conoce mucho de programacion funcional y todo eso
     */
//    @Bean
//    public WebClient webClient(WebClient.Builder builder) {
//        return builder
//                .filter(addJwtTokenFilter())
//                .build();
//    }

    @Bean
    public WebClient webClientPrivate(WebClient.Builder builder) {
        return builder
                .build();
    }

    /**
     * Filtro que intercepta todas las peticiones de WebClient
     * y agrega automáticamente el token JWT del contexto de seguridad
     */
    private ExchangeFilterFunction addJwtTokenFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .filter(auth -> auth instanceof JwtAuthenticationToken)
                        .cast(JwtAuthenticationToken.class)
                        .map(JwtAuthenticationToken::getToken)
                        .map(Jwt::getTokenValue)
                        .map(token -> ClientRequest.from(clientRequest)
                                .header("Authorization", "Bearer " + token)
                                .build())
                        .defaultIfEmpty(clientRequest)
        );
    }

}
