package com.noureddine.library.dto;

import java.time.LocalDate;

public class RegisterRequest {
    public String fullName;
    public String username;
    public String email;
    public String password;
    public String identifier;
    public String role;
    public LocalDate dateOfBirth;
    public String birthWilaya;
    public String cardBase64;
    public String contentType;
}
