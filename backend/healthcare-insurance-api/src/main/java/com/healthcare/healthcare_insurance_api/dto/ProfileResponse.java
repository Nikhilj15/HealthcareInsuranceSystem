package com.healthcare.healthcare_insurance_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.enums.UserStatus;

public class ProfileResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String role;

    private String status;

    private LocalDateTime createdAt;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================

    public ProfileResponse() {
    }


    // ==========================================
    // CONSTRUCTOR 1
    // USED BY ProfileService
    //
    // String role
    // String status
    // LocalDateTime createdAt
    // ==========================================

    public ProfileResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            LocalDate dateOfBirth,
            String gender,
            String address,
            String role,
            String status,
            LocalDateTime createdAt) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }


    // ==========================================
    // CONSTRUCTOR 2
    // USED BY CustomerService
    //
    // Role role
    // UserStatus status
    // ==========================================

    public ProfileResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            LocalDate dateOfBirth,
            String gender,
            String address,
            Role role,
            UserStatus status) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;

        this.address = address;

        this.role =
                role != null
                        ? role.name()
                        : null;

        this.status =
                status != null
                        ? status.name()
                        : null;

        this.createdAt = null;
    }


    // ==========================================
    // GETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    // ==========================================
    // SETTERS
    // ==========================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}