package com.bytesw.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de seguridad de Spring Security.
 * Define usuarios en memoria y la protección de endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Usuario en memoria para testing
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}password") // password sin encriptar para testing
                .roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    // Endpoint público para obtener la clave pública
                    .antMatchers("/oauth/token_key").permitAll()
                    // Todos los demás requieren autenticación
                    .anyRequest().authenticated()
                .and()
                .httpBasic() // Autenticación básica para clientes OAuth2
                .and()
                .csrf().disable(); // Deshabilitar CSRF para simplificar (en producción configurar apropiadamente)
    }
}
