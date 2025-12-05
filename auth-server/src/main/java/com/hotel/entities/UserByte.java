package com.hotel.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class UserByte {


    private String             username;

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
