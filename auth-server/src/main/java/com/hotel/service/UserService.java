package com.hotel.service;

import com.hotel.entities.UserByte;

public interface UserService {

    UserByte buscarUsuarioByUsername(String text);

    UserByte save(String username, String password);

}
