package com.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final WebClient webClient;


    public Mono<String> reservarHabitacionFilter() {
        String url = "http://127.0.0.1:8081/v1/habitaciones/reservar";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> reservarHabitacion() {
        String url = "http://127.0.0.1:8081/v1/habitaciones/reservar";

        return obtenerToken()
                .flatMap(token ->
                        webClient.get()
                                .uri(url)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToMono(String.class)
                );
    }

    private Mono<String> obtenerToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(Jwt::getTokenValue);
    }

}
