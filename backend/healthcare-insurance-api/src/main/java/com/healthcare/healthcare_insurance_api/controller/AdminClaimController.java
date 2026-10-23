package com.healthcare.healthcare_insurance_api.controller;

import com.healthcare.healthcare_insurance_api.dto.ClaimRejectRequest;
import com.healthcare.healthcare_insurance_api.dto.ClaimResponse;
import com.healthcare.healthcare_insurance_api.service.ClaimService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/admin/claims")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminClaimController {

    private final ClaimService claimService;

    public AdminClaimController(
            ClaimService claimService) {

        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {

        return ResponseEntity.ok(
                claimService.getAllClaims()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaimById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.getClaimById(id)
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ClaimResponse> approveClaim(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.approveClaim(id)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ClaimResponse> rejectClaim(
            @PathVariable Long id,
            @RequestBody ClaimRejectRequest request) {

        return ResponseEntity.ok(
                claimService.rejectClaim(
                        id,
                        request)
        );
    }
}