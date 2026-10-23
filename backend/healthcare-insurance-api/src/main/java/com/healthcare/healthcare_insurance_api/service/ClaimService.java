package com.healthcare.healthcare_insurance_api.service;

import com.healthcare.healthcare_insurance_api.dto.ClaimRejectRequest;
import com.healthcare.healthcare_insurance_api.dto.ClaimResponse;
import com.healthcare.healthcare_insurance_api.dto.ClaimSubmitRequest;
import com.healthcare.healthcare_insurance_api.entity.Claim;
import com.healthcare.healthcare_insurance_api.entity.Policy;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.ClaimStatus;
import com.healthcare.healthcare_insurance_api.enums.PolicyStatus;
import com.healthcare.healthcare_insurance_api.repository.ClaimRepository;
import com.healthcare.healthcare_insurance_api.repository.PolicyRepository;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    public ClaimService(
            ClaimRepository claimRepository,
            PolicyRepository policyRepository,
            UserRepository userRepository) {

        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // SUBMIT CLAIM
    // ==========================================

    public ClaimResponse submitClaim(
            String userEmail,
            ClaimSubmitRequest request) {

        if (request == null) {
            throw new RuntimeException(
                    "Claim submission request cannot be null");
        }

        if (request.getPolicyId() == null) {
            throw new RuntimeException(
                    "Policy ID is required");
        }

        if (request.getClaimAmount() == null ||
                request.getClaimAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Claim amount must be greater than zero");
        }

        if (request.getReason() == null ||
                request.getReason().trim().isEmpty()) {

            throw new RuntimeException(
                    "Claim reason is required");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Policy policy = policyRepository
                .findById(request.getPolicyId())
                .orElseThrow(() ->
                        new RuntimeException("Policy not found"));

        // Policy ownership validation
        if (!policy.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You can submit a claim only for your own policy");
        }

        // Policy must be ACTIVE
        if (policy.getStatus() != PolicyStatus.APPROVED &&
                policy.getStatus() != PolicyStatus.ACTIVE) {

            throw new RuntimeException(
                    "Claim can only be submitted for an approved or active policy");
        }

        // Claim amount cannot exceed coverage
        if (request.getClaimAmount()
                .compareTo(policy.getCoverageAmount()) > 0) {

            throw new RuntimeException(
                    "Claim amount cannot exceed policy coverage amount");
        }

        Claim claim = new Claim();

        claim.setClaimNumber(generateClaimNumber());

        claim.setPolicy(policy);

        claim.setUser(user);

        claim.setClaimAmount(
                request.getClaimAmount());

        claim.setReason(
                request.getReason().trim());

        claim.setDescription(
                request.getDescription());

        claim.setClaimDate(
                LocalDate.now());

        claim.setStatus(
                ClaimStatus.PENDING);

        Claim savedClaim =
                claimRepository.save(claim);

        return convertToResponse(savedClaim);
    }

    // ==========================================
    // USER - MY CLAIMS
    // ==========================================

    public List<ClaimResponse> getMyClaims(
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return claimRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // USER - CLAIM BY ID
    // ==========================================

    public ClaimResponse getMyClaimById(
            Long claimId,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Claim claim = claimRepository
                .findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException("Claim not found"));

        if (!claim.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You can access only your own claim");
        }

        return convertToResponse(claim);
    }

    // ==========================================
    // ADMIN - ALL CLAIMS
    // ==========================================

    public List<ClaimResponse> getAllClaims() {

        return claimRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ADMIN - CLAIM BY ID
    // ==========================================

    public ClaimResponse getClaimById(
            Long claimId) {

        Claim claim = claimRepository
                .findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException("Claim not found"));

        return convertToResponse(claim);
    }

    // ==========================================
    // ADMIN - APPROVE CLAIM
    // ==========================================

    public ClaimResponse approveClaim(
            Long claimId) {

        Claim claim = claimRepository
                .findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException("Claim not found"));

        if (claim.getStatus() != ClaimStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending claims can be approved");
        }

        claim.setStatus(
                ClaimStatus.APPROVED);

        claim.setApprovedAt(
                LocalDateTime.now());

        Claim updatedClaim =
                claimRepository.save(claim);

        return convertToResponse(updatedClaim);
    }

    // ==========================================
    // ADMIN - REJECT CLAIM
    // ==========================================

    public ClaimResponse rejectClaim(
            Long claimId,
            ClaimRejectRequest request) {

        if (request == null ||
                request.getReason() == null ||
                request.getReason().trim().isEmpty()) {

            throw new RuntimeException(
                    "Rejection reason is required");
        }

        Claim claim = claimRepository
                .findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException("Claim not found"));

        if (claim.getStatus() != ClaimStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending claims can be rejected");
        }

        claim.setStatus(
                ClaimStatus.REJECTED);

        claim.setRejectedAt(
                LocalDateTime.now());

        claim.setRejectionReason(
                request.getReason().trim());

        Claim updatedClaim =
                claimRepository.save(claim);

        return convertToResponse(updatedClaim);
    }

    // ==========================================
    // CLAIM NUMBER
    // ==========================================

    private String generateClaimNumber() {

        String claimNumber;

        do {

            claimNumber =
                    "CLM-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

        } while (
                claimRepository
                        .findByClaimNumber(claimNumber)
                        .isPresent()
        );

        return claimNumber;
    }

    // ==========================================
    // ENTITY -> RESPONSE
    // ==========================================

    private ClaimResponse convertToResponse(
            Claim claim) {

        ClaimResponse response =
                new ClaimResponse();

        response.setId(
                claim.getId());

        response.setClaimNumber(
                claim.getClaimNumber());

        response.setPolicyId(
                claim.getPolicy().getId());

        response.setPolicyNumber(
                claim.getPolicy().getPolicyNumber());

        response.setUserId(
                claim.getUser().getId());

        response.setUserName(
                claim.getUser().getFullName());

        response.setUserEmail(
                claim.getUser().getEmail());

        response.setClaimAmount(
                claim.getClaimAmount());

        response.setReason(
                claim.getReason());

        response.setDescription(
                claim.getDescription());

        response.setClaimDate(
                claim.getClaimDate());

        response.setStatus(
                claim.getStatus());

        response.setApprovedAt(
                claim.getApprovedAt());

        response.setRejectedAt(
                claim.getRejectedAt());

        response.setRejectionReason(
                claim.getRejectionReason());

        return response;
    }
}