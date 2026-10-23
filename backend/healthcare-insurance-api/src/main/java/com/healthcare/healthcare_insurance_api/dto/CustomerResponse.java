package com.healthcare.healthcare_insurance_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String status;

    private LocalDateTime createdAt;


    // Default Constructor
    public CustomerResponse() {
    }


    // Parameterized Constructor
    public CustomerResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            LocalDate dateOfBirth,
            String gender,
            String address,
            String status,
            LocalDateTime createdAt) {

        this.id = id;

        this.fullName = fullName;

        this.email = email;

        this.phone = phone;

        this.dateOfBirth = dateOfBirth;

        this.gender = gender;

        this.address = address;

        this.status = status;

        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}