package com.noureddine.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String birthWilaya;
    private String identifier;
    private LocalDate joinDate;
    private String accountStatus;
    private LocalDate lastActiveDate;
    private String department;
    @Lob
    private byte[] studentCard;
    private String studentCardContentType;
    private int numberOfBorrowings;

    public User(String username, String password, String role, String birthWilaya, LocalDate joinDate, String accountStatus, LocalDate lastActiveDate) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.birthWilaya = birthWilaya;
        this.joinDate = joinDate;
        this.accountStatus = accountStatus;
        this.lastActiveDate = lastActiveDate;
    }

    public User() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public byte[] getStudentCard() {
        return studentCard;
    }

    public void setStudentCard(byte[] studentCard) {
        this.studentCard = studentCard;
    }

    public String getStudentCardContentType() {
        return studentCardContentType;
    }

    public void setStudentCardContentType(String studentCardContentType) {
        this.studentCardContentType = studentCardContentType;
    }

    public String getBirthWilaya() {
        return birthWilaya;
    }

    public void setBirthWilaya(String birthWilaya) {
        this.birthWilaya = birthWilaya;
    }

    public int getNumberOfBorrowings() {
        return numberOfBorrowings;
    }

    public void setNumberOfBorrowings(int numberOfBorrowings) {
        this.numberOfBorrowings = numberOfBorrowings;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
