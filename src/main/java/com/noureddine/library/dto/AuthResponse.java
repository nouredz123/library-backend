package com.noureddine.library.dto;

public class AuthResponse {
    public String token;
    public String role;
    public Long id;

    public AuthResponse(String token, String role, Long id) {
        this.token = token;
        this.role = role;
        this.id = id;
    }
}
