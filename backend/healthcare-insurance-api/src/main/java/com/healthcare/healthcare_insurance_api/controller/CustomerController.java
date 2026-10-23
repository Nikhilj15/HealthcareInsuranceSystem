package com.healthcare.healthcare_insurance_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.healthcare.healthcare_insurance_api.dto.ProfileResponse;
import com.healthcare.healthcare_insurance_api.dto.ProfileUpdateRequest;
import com.healthcare.healthcare_insurance_api.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(
            CustomerService customerService) {

        this.customerService = customerService;
    }


    // =================================
    // GET LOGGED-IN USER PROFILE
    // =================================

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication) {

        String email =
                authentication.getName();

        ProfileResponse response =
                customerService.getProfile(email);

        return ResponseEntity.ok(response);
    }


    // =================================
    // UPDATE LOGGED-IN USER PROFILE
    // =================================

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody ProfileUpdateRequest request) {

        String email =
                authentication.getName();

        ProfileResponse response =
                customerService.updateProfile(
                        email,
                        request
                );

        return ResponseEntity.ok(response);
    }
}