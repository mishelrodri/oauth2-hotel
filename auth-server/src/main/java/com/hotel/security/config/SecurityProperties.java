package com.hotel.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    private List<String> exposedAntMatchers =  new ArrayList<>();
}
