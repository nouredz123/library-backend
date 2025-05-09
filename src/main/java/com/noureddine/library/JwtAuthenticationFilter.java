package com.noureddine.library;

import com.noureddine.library.entity.User;
import com.noureddine.library.repository.UserRepository;
import com.noureddine.library.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(email).orElseThrow();
            if (jwtService.isTokenValid(token, user)) {
                var role = user.getRole();
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role.toUpperCase();
                }
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );
                System.out.println("role: " + role);
                System.out.println("Authorities: " + authToken.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

                LocalDate lastActiveDate = user.getLastActiveDate() != null
                        ? user.getLastActiveDate()
                        : null;

                if (lastActiveDate == null || !lastActiveDate.isEqual(LocalDate.now())) {
                    user.setLastActiveDate(LocalDate.now());
                    userRepository.save(user);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
