package com.hotel.security.filters;

import com.hotel.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CreateUserFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("🔵 FILTRO EJECUTADO - " + request.getMethod() + " " + request.getRequestURI());
        String username = request.getHeader("X-user-name");
        String password = request.getHeader("X-user-password");

        userService.save(username, password);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Solo ejecutar en /oauth2/token
        String path = request.getRequestURI();
        return !path.equals("/oauth2/token");
    }
}
