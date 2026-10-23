package com.healthcare.healthcare_insurance_api.controller;

import com.healthcare.healthcare_insurance_api.dto.ProfileResponse;
import com.healthcare.healthcare_insurance_api.dto.UpdateProfileRequest;
import com.healthcare.healthcare_insurance_api.service.ProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserProfileController {

    private final ProfileService profileService;

    public UserProfileController(
            ProfileService profileService) {

        this.profileService = profileService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProfileResponse> getMyProfile(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                profileService.getProfile(email)
        );
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                profileService.updateProfile(
                        email,
                        request
                )
        );
    }
}