package com.healthcare.healthcare_insurance_api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Full name must be between 2 and 100 characters"
    )
    private String fullName;


    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(
        max = 150,
        message = "Email must not exceed 150 characters"
    )
    private String email;


    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Phone number must be a valid 10 digit Indian mobile number"
    )
    private String phone;


    @NotBlank(message = "Password is required")
    @Size(
        min = 6,
        max = 100,
        message = "Password must be between 6 and 100 characters"
    )
    private String password;


    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;


    @NotBlank(message = "Gender is required")
    @Pattern(
        regexp = "^(Male|Female|Other)$",
        message = "Gender must be Male, Female or Other"
    )
    private String gender;


    @NotBlank(message = "Address is required")
    @Size(
        max = 500,
        message = "Address must not exceed 500 characters"
    )
    private String address;


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


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}