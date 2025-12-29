package com.hotel.security.config;

import com.hotel.entities.UserByte;
import com.hotel.security.filters.AuthenticationUserFilter;
import com.hotel.security.filters.CreateUserFilter;
import com.hotel.service.UserService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final AuthenticationUserFilter authenticationUserFilter;
    private final UserService userService;

    /**
     * Configuración específica del servidor de autorización
     * Esto activa los endpoints de OAuth2 (/oauth2/token, etc.)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();


        authorizationServerConfigurer.tokenEndpoint(tokenEndpoint ->
                tokenEndpoint.accessTokenResponseHandler(customTokenResponseHandler())
        );

        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())    // Enable OpenID Connect 1.0
                )
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationUserFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customTokenResponseHandler() {
        return new CustomTokenResponseHandler(userService);
    }

    /**
     * Define QUÉ CLIENTES pueden pedir tokens
     * Un "cliente" es una aplicación que usa tu servidor (ej: tu app Angular)
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("my-client")  // Identificador del cliente
                .clientSecret("{noop}secret")  // Contraseña del cliente
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)  // Tipo de flujo
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofSeconds(3000))
                        .build()
                )
                .scope("read")  // Permisos que puede solicitar
                .scope("write")
                .build();

        return new InMemoryRegisteredClientRepository(client);
    }

//    @Bean
//    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
//        return context -> {
////            Authentication principal = context.getPrincipal();
//            context.getClaims().claim("username", "manolo");
//        };
//    }

@Bean
public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserService userService) {
    return context -> {

        // Obtén el request actual
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            Authentication userAuthentication = (Authentication) request.getAttribute("USER_AUTHENTICATION");

            if (userAuthentication != null) {
                String username = userAuthentication.getName();


                UserByte userByte = userService.buscarUsuarioByUsername(username);

                Set<String> authorities = AuthorityUtils.authorityListToSet(
                        userAuthentication.getAuthorities()
                );

                context.getClaims().claim("username", username);
                context.getClaims().claim("sub", username);
                context.getClaims().claim("full_name", userByte.getUserFullName());
                context.getClaims().claim("user_id", userByte.getId()); // Si tienes el ID
                context.getClaims().claim("type_token", userByte.getTypeToken());
                context.getClaims().claim("authorities", authorities);



            } else {
                log.warn("⚠️ No hay autenticación de usuario - usando solo client credentials");
                context.getClaims().claim("username", null);
                context.getClaims().claim("user_authenticated", false);
            }
        }
    };
}

    /**
     * Configuración de los tokens JWT
     * Define CÓMO se firman y validan los tokens
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRSAKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * Genera un par de llaves RSA (pública/privada)
     * La llave privada firma los tokens
     * La llave pública los verifica
     */
    private RSAKey generateRSAKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Decodificador de tokens JWT
     * Necesario para que el servidor pueda leer los tokens que genera
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Configuración de emisor del token
     * Define la URL del servidor de autorización
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }

}
