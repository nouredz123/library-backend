package com.noureddine.library.service;

import com.noureddine.library.dto.AuthRequest;
import com.noureddine.library.dto.AuthResponse;
import com.noureddine.library.dto.RegisterRequest;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.InvalidPasswordException;
import com.noureddine.library.exception.UsernameAlreadyExistsException;
import com.noureddine.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;

    public AuthResponse register(RegisterRequest req) {
        //User existUser = userRepository.findByUsername(req.username).orElseThrow(()-> new UsernameAlreadyExistsException("A user with this username already exists"));
        if(req.password == null || req.password.isEmpty()){throw new InvalidPasswordException("Password can not be empty");}
        if(req.password.length() < 8 ) {throw new InvalidPasswordException("Password must be at least 8 chars");}
        User user = new User();
        user.setFullName(req.fullName);
        user.setUsername(req.username);
        user.setEmail(req.email);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setIdentifier(req.identifier);
        user.setJoinDate(LocalDate.now());
        user.setRole("ROLE_" + req.role.toUpperCase());
        user.setAccountStatus("PENDING");
        user.setLastActiveDate(LocalDate.now());
        User savedUser = repo.save(user);
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt, user.getRole().replace("ROLE_", ""), savedUser.getId());
    }

    public AuthResponse authenticate(AuthRequest req) {
        User user = repo.findByUsername(req.username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.password, user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");
        LocalDate lastActiveDate = user.getLastActiveDate() != null
                ? user.getLastActiveDate()
                : null;

        if (lastActiveDate == null || !lastActiveDate.isEqual(LocalDate.now())) {
            user.setLastActiveDate(LocalDate.now());
            userRepository.save(user);
        }

        var jwt = jwtService.generateToken(user);

        return new AuthResponse(jwt, user.getRole().replace("ROLE_", ""), user.getId());
    }
}

