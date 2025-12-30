package com.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final RestTemplate restTemplate;

    public String reservarHabitacion(){
        String url = "http://127.0.0.1:8081/v1/habitaciones/reservar";

        return restTemplate.getForObject(url, String.class);
    }

}
