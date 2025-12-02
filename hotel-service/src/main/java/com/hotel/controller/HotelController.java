package com.hotel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelController {

    @GetMapping
    public String Hola(){
        return "<h1>Hola \uD83D\uDE4C</h1>";
    }

}
