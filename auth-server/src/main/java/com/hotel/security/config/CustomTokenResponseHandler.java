package com.hotel.security.config;

import com.hotel.entities.UserByte;
import com.hotel.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class CustomTokenResponseHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter =
            new OAuth2AccessTokenResponseHttpMessageConverter();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
                (OAuth2AccessTokenAuthenticationToken) authentication;

        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();

        Authentication userAuthentication = (Authentication) request.getAttribute("USER_AUTHENTICATION");
        Set<String> authoritiesSet = AuthorityUtils.authorityListToSet(userAuthentication.getAuthorities());
        UserByte userByte = userService.buscarUsuarioByUsername(userAuthentication.getName());


        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("user_name", userAuthentication.getName());
        additionalParameters.put("sub", userAuthentication.getName());
        additionalParameters.put("full_name", userByte.getUserFullName());
        additionalParameters.put("typeToken", userByte.getTypeToken());
        additionalParameters.put("authorities", authoritiesSet);


        OAuth2AccessTokenResponse.Builder builder =
                OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                        .tokenType(accessToken.getTokenType())
                        .scopes(accessToken.getScopes());

        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(
                    accessToken.getIssuedAt(),
                    accessToken.getExpiresAt()
            ));
        }

        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }

        // Agregar parámetros adicionales
        builder.additionalParameters(additionalParameters);

        OAuth2AccessTokenResponse tokenResponse = builder.build();

        // Escribir la respuesta
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        accessTokenHttpResponseConverter.write(tokenResponse, null, httpResponse);
    }
}
