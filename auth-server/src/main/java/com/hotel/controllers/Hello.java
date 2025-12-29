package com.hotel.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {


    @GetMapping("/hello")
    public ResponseEntity<?> hello(){
        return ResponseEntity.ok("Helllo");
    }

    @GetMapping("/public")
    public ResponseEntity<?> publico(){
        return ResponseEntity.ok("Endpoint publico");
    }

}
