package com.hotel.security.filters;

import com.hotel.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationUserFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String username = request.getHeader("X-user-name");
        String password = request.getHeader("X-user-password");

        if (username == null || password == null) {
//            filterChain.doFilter(request, response);
            return;
        }

        try {

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authentication = authenticationManager.authenticate(authRequest);


//            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("USER_AUTHENTICATION", authentication);
            log.info("✅ Usuario autenticado: {}", username);

        } catch (AuthenticationException e) {
            log.error("❌ Fallo de autenticación para usuario: {}", username);
            log.error(e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid credentials\"}");
            return; // No continúa con la cadena de filtros
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.equals("/oauth2/token");
    }
}
