package com.noureddine.library.dto;

import java.time.LocalDate;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String identifier;
    private String role;
    private String department;
    private LocalDate dateOfBirth;
    private String birthWilaya;
    private String cardBase64;
    private String contentType;

    public RegisterRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBirthWilaya() {
        return birthWilaya;
    }

    public void setBirthWilaya(String birthWilaya) {
        this.birthWilaya = birthWilaya;
    }

    public String getCardBase64() {
        return cardBase64;
    }

    public void setCardBase64(String cardBase64) {
        this.cardBase64 = cardBase64;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
