package com.noureddine.library.service;

import com.noureddine.library.dto.AuthRequest;
import com.noureddine.library.dto.AuthResponse;
import com.noureddine.library.dto.RegisterRequest;
import com.noureddine.library.dto.RegisterRequestAdmin;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.InvalidArgumentException;
import com.noureddine.library.exception.InvalidPasswordException;
import com.noureddine.library.exception.EmailAlreadyExistsException;
import com.noureddine.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Base64;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        userRepository.findByEmail(req.email)
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("A user with this email already exists");
                });
        if(req.password == null || req.password.isEmpty()){throw new InvalidPasswordException("Please enter your password.");}
        if(req.password.length() < 8 ) {throw new InvalidPasswordException("Your password must be at least 8 characters long.");}
        User user = new User();
        user.setFullName(req.fullName);
        user.setUsername(req.username);
        user.setEmail(req.email);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setIdentifier(req.identifier);
        user.setJoinDate(LocalDate.now());
        user.setBirthWilaya(req.birthWilaya);
        user.setDateOfBirth(req.dateOfBirth);
        user.setRole("ROLE_" + req.role.toUpperCase());
        user.setAccountStatus(req.role.equalsIgnoreCase("MEMBER") ? "PENDING" : "APPROVED" );
        user.setLastActiveDate(LocalDate.now());
        user.setStudentCard(Base64.getDecoder().decode(req.cardBase64));
        user.setStudentCardContentType(req.contentType);
        User savedUser = userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt, user.getRole().replace("ROLE_", ""), savedUser.getId());
    }
    public AuthResponse registerAdmin(RegisterRequestAdmin req) {
        String verificationCode = "adminVerificationCode";
        if(!verificationCode.equals(req.adminCode)){
            throw new InvalidArgumentException("Please provide a valid admin code to continue.");
        }
        userRepository.findByEmail(req.email)
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("A user with this email already exists");
                });
        if(req.password == null || req.password.isEmpty()){throw new InvalidPasswordException("Please enter your password.");}
        if(req.password.length() < 8 ) {throw new InvalidPasswordException("Your password must be at least 8 characters long.");}
        User user = new User();
        user.setFullName(req.fullName);
        user.setEmail(req.email);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setRole("ROLE_STAFF");
        String jwt = jwtService.generateToken(user);
        User savedUser = userRepository.save(user);
        return new AuthResponse(jwt, user.getRole().replace("ROLE_", ""), savedUser.getId());
    }

    public AuthResponse authenticate(AuthRequest req) {
        User user = userRepository.findByEmail(req.email)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with that email address."));
        if (!passwordEncoder.matches(req.password, user.getPassword()))
            throw new BadCredentialsException("Incorrect username or password. Please try again.");
        LocalDate lastActiveDate = user.getLastActiveDate() != null
                ? user.getLastActiveDate()
                : null;

        if (lastActiveDate == null || !lastActiveDate.isEqual(LocalDate.now())) {
            user.setLastActiveDate(LocalDate.now());
            userRepository.save(user);
        }

        String jwt = jwtService.generateToken(user);

        return new AuthResponse(jwt, user.getRole().replace("ROLE_", ""), user.getId());
    }

    public boolean isEmailValid(String email) {
        Boolean isEmailExists = userRepository.existsByEmail(email);
        return !isEmailExists;
    }
}

