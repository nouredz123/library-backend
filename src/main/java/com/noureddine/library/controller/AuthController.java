package com.noureddine.library.controller;

import com.noureddine.library.dto.AuthRequest;
import com.noureddine.library.dto.AuthResponse;
import com.noureddine.library.dto.RegisterRequest;
import com.noureddine.library.dto.RegisterRequestAdmin;
import com.noureddine.library.service.AuthService;
import com.noureddine.library.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }
    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestBody RegisterRequestAdmin req) {
        return ResponseEntity.ok(authService.registerAdmin(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        return ResponseEntity.ok(authService.authenticate(req));
    }
    @GetMapping("/validate/token")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token, @RequestParam String email){
        return ResponseEntity.ok(jwtService.isTokenValid(token, email));
    }
    @GetMapping("/validate/email")
    public ResponseEntity<Boolean> validateEmail(@RequestParam String email){
        return ResponseEntity.ok(authService.isEmailValid(email));
    }
}
