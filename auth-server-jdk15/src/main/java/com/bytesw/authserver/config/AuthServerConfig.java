package com.bytesw.authserver.config;

import com.bytesw.authserver.util.KeyPairProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Configuración del Authorization Server OAuth2.
 * Define los clientes, endpoints y la generación de tokens JWT.
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KeyPairProvider keyPairProvider;

    /**
     * Configura los clientes OAuth2 que pueden solicitar tokens.
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // Cliente 1: agreement-service (microservicio)
                .withClient("agreement-service")
                .secret("{noop}secret")
                .authorizedGrantTypes("client_credentials", "password", "refresh_token")
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600) // 1 hora
                .refreshTokenValiditySeconds(86400) // 24 horas
                
                .and()
                
                // Cliente 2: web-app (aplicación web)
                .withClient("web-app")
                .secret("{noop}web-secret")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .scopes("read", "write")
                .redirectUris("http://localhost:4200/callback")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(86400)
                
                .and()
                
                // Cliente 3: mobile-app (aplicación móvil)
                .withClient("mobile-app")
                .secret("{noop}mobile-secret")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write")
                .accessTokenValiditySeconds(7200) // 2 horas
                .refreshTokenValiditySeconds(604800); // 7 días
    }

    /**
     * Configura los endpoints y el token store con JWT.
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter());
    }

    /**
     * Configura la seguridad de los endpoints OAuth2.
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                .tokenKeyAccess("permitAll()") // /oauth/token_key es público
                .checkTokenAccess("isAuthenticated()"); // /oauth/check_token requiere autenticación
                // .allowFormAuthenticationForClients(); // Permite autenticación de clientes via form
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // Usa el par de claves RSA generado para firmar los JWT
        converter.setKeyPair(keyPairProvider.getKeyPair());
        return converter;
    }
}
