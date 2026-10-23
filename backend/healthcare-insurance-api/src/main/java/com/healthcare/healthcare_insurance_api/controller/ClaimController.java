package com.healthcare.healthcare_insurance_api.controller;

import com.healthcare.healthcare_insurance_api.dto.ClaimRejectRequest;
import com.healthcare.healthcare_insurance_api.dto.ClaimResponse;
import com.healthcare.healthcare_insurance_api.dto.ClaimSubmitRequest;
import com.healthcare.healthcare_insurance_api.service.ClaimService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "http://localhost:4200")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(
            ClaimService claimService) {

        this.claimService = claimService;
    }

    // ==========================================
    // USER - SUBMIT CLAIM
    // ==========================================

    @PostMapping("/submit")
    public ResponseEntity<ClaimResponse> submitClaim(
            @RequestBody ClaimSubmitRequest request,
            Authentication authentication) {

        String email =
                authentication.getName();

        ClaimResponse response =
                claimService.submitClaim(
                        email,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // USER - MY CLAIMS
    // ==========================================

    @GetMapping("/my")
    public ResponseEntity<List<ClaimResponse>> getMyClaims(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                claimService.getMyClaims(email)
        );
    }

    // ==========================================
    // USER - MY CLAIM BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getMyClaimById(
            @PathVariable Long id,
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                claimService.getMyClaimById(
                        id,
                        email
                )
        );
    }

    // ==========================================
    // ADMIN - ALL CLAIMS
    // ==========================================

    @GetMapping("/admin/all")
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {

        return ResponseEntity.ok(
                claimService.getAllClaims()
        );
    }

    // ==========================================
    // ADMIN - CLAIM BY ID
    // ==========================================

    @GetMapping("/admin/{id}")
    public ResponseEntity<ClaimResponse> getClaimById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.getClaimById(id)
        );
    }

    // ==========================================
    // ADMIN - APPROVE CLAIM
    // ==========================================

    @PutMapping("/{id}/approve")
    public ResponseEntity<ClaimResponse> approveClaim(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.approveClaim(id)
        );
    }

    // ==========================================
    // ADMIN - REJECT CLAIM
    // ==========================================

    @PutMapping("/{id}/reject")
    public ResponseEntity<ClaimResponse> rejectClaim(
            @PathVariable Long id,
            @RequestBody ClaimRejectRequest request) {

        return ResponseEntity.ok(
                claimService.rejectClaim(
                        id,
                        request
                )
        );
    }
}

