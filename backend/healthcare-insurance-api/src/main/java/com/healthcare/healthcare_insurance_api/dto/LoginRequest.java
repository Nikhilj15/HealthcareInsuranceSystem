package com.healthcare.healthcare_insurance_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Login type is required")
    @Pattern(
        regexp = "USER|ADMIN",
        message = "Login type must be USER or ADMIN"
    )
    private String loginType;

    public LoginRequest() {
    }

    public LoginRequest(
            String email,
            String password,
            String loginType) {
        this.email = email;
        this.password = password;
        this.loginType = loginType;
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

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}