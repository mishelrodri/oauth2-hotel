package com.hotel.security.services;

import com.hotel.entities.UserByte;
import com.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserByte userByte = userService.buscarUsuarioByUsername(username);


        return User.withUsername(userByte.getUsername())
                .password(userByte.getPassword())
                .authorities(Arrays.asList(new SimpleGrantedAuthority("APP_USER")))
                .build();
    }

}
