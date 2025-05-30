package com.noureddine.library.service;

import com.noureddine.library.dto.AuthRequest;
import com.noureddine.library.dto.AuthResponse;
import com.noureddine.library.dto.RegisterRequest;
import com.noureddine.library.dto.RegisterRequestAdmin;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.*;
import com.noureddine.library.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
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
        //check for null parameters
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            throw new InvalidEmailException("Email is required.");
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new InvalidPasswordException("Password is required.");
        }
        if (req.getPassword().length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters.");
        }
        if (req.getFullName() == null || req.getFullName().isEmpty()) {
            throw new InvalidDataException("Full name is required.");
        }
        if (req.getRole() == null || req.getRole().isEmpty()) {
            throw new InvalidRoleException("Role is required.");
        }
        if (req.getIdentifier() == null || req.getIdentifier().isEmpty()) {
            throw new InvalidDataException("Identifier is required.");
        }
        //search for the user by its email
        userRepository.findByEmail(req.getEmail())
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("A user with this email already exists");
                });
        //create a user instance with the provided informations
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setIdentifier(req.getIdentifier());
        user.setJoinDate(LocalDate.now());
        user.setBirthWilaya(req.getBirthWilaya());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setRole("ROLE_" + req.getRole().toUpperCase());
        user.setAccountStatus(req.getRole().equalsIgnoreCase("MEMBER") ? "PENDING" : "APPROVED" );
        user.setLastActiveDate(LocalDate.now());
        //make sure the card is not null and is a valid base64 format
        if (req.getCardBase64() != null && !req.getCardBase64().isEmpty()) {
            try {
                user.setStudentCard(Base64.getDecoder().decode(req.getCardBase64()));
                user.setStudentCardContentType(req.getContentType());
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Invalid base64 format for student card.");
            }
        }
        //save the new user in the database
        User savedUser = userRepository.save(user);
        //generate the user token
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt, user.getRole().replace("ROLE_", ""), savedUser.getId());
    }
    public AuthResponse registerAdmin(RegisterRequestAdmin adminRequest) {
        //code for staff registration to make sure not anyone can register as a staff
        String verificationCode = "IAgreeThatIngAreTheBest";
        if(!verificationCode.equals(adminRequest.getAdminCode())){
            throw new InvalidArgumentException("Please provide a valid admin code to continue.");
        }
        //create a RegisterRequest and call the register method
        RegisterRequest request = new RegisterRequest();
        request.setFullName(adminRequest.getFullName());
        request.setEmail(adminRequest.getEmail());
        request.setPassword(adminRequest.getPassword());
        request.setFullName(request.getFullName());
        request.setRole("STAFF");
        return register(request);
    }

    public AuthResponse authenticate(AuthRequest req) {
        //check for null parameters
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            throw new InvalidEmailException("Email is required.");
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new InvalidPasswordException("Password is required.");
        }
        //search for the user by its email
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidEmailException("No account found with that email address."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Incorrect username or password. Please try again.");
        //get the last active date for the user
        LocalDate lastActiveDate = user.getLastActiveDate() != null
                ? user.getLastActiveDate()
                : null;
        //update the last active date if it is null or not today
        if (lastActiveDate == null || !lastActiveDate.isEqual(LocalDate.now())) {
            user.setLastActiveDate(LocalDate.now());
            userRepository.save(user);
        }
        //generate user token
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt, user.getRole().replace("ROLE_", ""), user.getId());
    }

    public boolean isEmailValid(String email) {
        Boolean isEmailExists = userRepository.existsByEmail(email);
        return !isEmailExists;
    }
}

