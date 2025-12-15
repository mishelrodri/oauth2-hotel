package com.hotel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Builder
@Entity
@Table(name = "users")
@Getter
public class UserByte {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String             username;

    @JsonIgnore
    private String             password;

//    private String             deviceStatus;

    private boolean            accountNonExpired;

    private boolean            accountNonLocked;

    private boolean            credentialsNonExpired;

    private boolean            enabled;

//    private String             sessionID;

//    private String             remoteIP;
//
//    private String             userAgent;
//
//    private String             publicKey;
//
//    private String             userFullName;

    private String             userEmail;
//
//    private boolean            tokenRequired      = true;
//
//    private boolean            facephi            = true;
//
//    private LocalDateTime time               = LocalDateTime.now();
//
//    private Collection<String> grantedAuthorities = Arrays.asList("APP_USER");
//
//    private String             totpKey;
//
//    private String             clock;
//
//    private String             sessionToken;
//
//    private String             typeToken;
//
//    private String             token;
//
//    private String             clientType;



}
