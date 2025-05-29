package com.noureddine.library;

import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
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
//this filter runs once for every request and checks for a valid JWT token.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    //this method is called for every incoming HTTP request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        //get the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        //if there is no Authorization header or it doesn't start with "Bearer ", skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        //extract the token from the header (begin after "Bearer")
        String token = authHeader.substring(7);
        //use JwtService to extract the email from the token
        String email = jwtService.extractEmail(token);
        //if we successfully get an email and the user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //find the user by email from the database
            User user = userRepository.findByEmail(email).orElseThrow();
            //validate the token using JwtService and the user's details
            if (jwtService.isTokenValid(token, user)) {
                String role = user.getRole();
                //spring security expects roles to start with "ROLE_", so we add it if needed
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role.toUpperCase();
                }
                //create an authentication token with user info and authority
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );
                System.out.println("role: " + role);
                System.out.println("Authorities: " + authToken.getAuthorities());
                //set this authentication info into the Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
                //updating user's last active date
                LocalDate lastActiveDate = user.getLastActiveDate() != null
                        ? user.getLastActiveDate()
                        : null;
                //if the last active date is not today, update it
                if (lastActiveDate == null || !lastActiveDate.isEqual(LocalDate.now())) {
                    user.setLastActiveDate(LocalDate.now());
                    userRepository.save(user);
                }
            }
        }
        //continue with the next filter or controller
        chain.doFilter(request, response);
    }
}
