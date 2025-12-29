package com.hotel.service;

import com.hotel.entities.UserByte;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSericeImpl implements UserService {

    List<UserByte> usuarios;

    @PostConstruct
    public void init() {
        usuarios = new ArrayList<>();
//        usuarios.add(UserByte.builder().username("pepe").password("{noop}123").build());
    }


    @Override
    public UserByte buscarUsuarioByUsername(String text) {
        return usuarios.stream().filter((u)-> u.getUsername().equals(text)).findFirst()
                .orElseThrow(()-> new RuntimeException("NO EXISTE"));
    }

    @Override
    public UserByte save(String username, String password) {
        UserByte newUser = UserByte.builder()
                .username(username)
                .password("{noop}"+password)
                .userEmail(username + "@gmail.com")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
         usuarios.add(newUser);

         return newUser;
    }
}
