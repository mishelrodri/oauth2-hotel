package com.hotel.repositories;

import com.hotel.entities.UserByte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserByte, Long> {

    Optional<UserByte> findByUsername(String username);

}
