package com.hotel.service;

import com.hotel.entities.UserByte;
import com.hotel.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class UserServiceDataBaseImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserByte buscarUsuarioByUsername(String text) {
        return userRepository.findByUsername(text).orElseThrow(()-> new RuntimeException("El usuario ono existe"));
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


        return userRepository.save(newUser);
    }
}
