
package com.healthcare.healthcare_insurance_api.dto;

public class LoginResponse {

    private String token;
    private String type;
    private long expiresIn;
    private String role;

    // Default constructor
    public LoginResponse() {
    }

    // Constructor with all four fields
    public LoginResponse(
            String token,
            String type,
            long expiresIn,
            String role) {

        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
