package com.hotel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserByte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String             userFullName;

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
    private String             typeToken;
//
//    private String             token;
//
//    private String             clientType;



}
