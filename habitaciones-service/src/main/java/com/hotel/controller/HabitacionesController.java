package com.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/habitaciones")
public class HabitacionesController {

    @GetMapping("/reservar")
    public ResponseEntity<?> reservar(){
        return ResponseEntity.ok("Habitacion reservada");
    }


}
