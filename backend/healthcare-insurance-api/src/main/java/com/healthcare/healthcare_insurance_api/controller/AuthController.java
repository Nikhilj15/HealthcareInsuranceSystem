package com.healthcare.healthcare_insurance_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.healthcare.healthcare_insurance_api.dto.LoginRequest;
import com.healthcare.healthcare_insurance_api.dto.LoginResponse;
import com.healthcare.healthcare_insurance_api.dto.RegisterRequest;
import com.healthcare.healthcare_insurance_api.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // -----------------------------------------
    // LOGIN
    // -----------------------------------------

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    // -----------------------------------------
    // REGISTER
    // -----------------------------------------

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        String message =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(message);
    }
}

