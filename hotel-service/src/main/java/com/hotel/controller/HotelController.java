package com.hotel.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/hotel")
public class HotelController {

    @GetMapping("/privado")
    public String Hola(){
        return "<h1>Hola 🔒</h1>";
    }


    @GetMapping("/test")
    public String publico(){
        return "<h1>Hola 👋👋</h1>";
    }

    @GetMapping("/profile")
    public String getProfile(Principal principal) {

        Jwt jwt = (Jwt) ((JwtAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication()).getToken();

//        return UserInfo.builder()
//                .username(jwt.getClaimAsString("username"))
//                .userId(jwt.getClaim("user_id"))
//                .email(jwt.getClaimAsString("email"))
//                .build();

//        return jwt.getClaimAsString("username");
        return principal.getName();
    }
}
