package com.healthcare.healthcare_insurance_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.healthcare.healthcare_insurance_api.dto.PolicyPlanRequest;
import com.healthcare.healthcare_insurance_api.entity.PolicyPlan;
import com.healthcare.healthcare_insurance_api.enums.PolicyPlanStatus;
import com.healthcare.healthcare_insurance_api.repository.PolicyPlanRepository;

@Service
public class PolicyPlanService {

    private final PolicyPlanRepository policyPlanRepository;

    public PolicyPlanService(
            PolicyPlanRepository policyPlanRepository) {

        this.policyPlanRepository =
                policyPlanRepository;
    }

    // ==========================================
    // GET ALL PLANS
    // ==========================================

    public List<PolicyPlan> getAllPlans() {

        return policyPlanRepository.findAll();
    }

    // ==========================================
    // GET ACTIVE PLANS
    // ==========================================

    public List<PolicyPlan> getActivePlans() {

        return policyPlanRepository.findByStatus(
                PolicyPlanStatus.ACTIVE
        );
    }

    // ==========================================
    // GET PLAN BY ID
    // ==========================================

    public PolicyPlan getPlanById(Long id) {

        if (id == null || id <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid policy plan ID."
            );
        }

        return policyPlanRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Policy plan not found."
                        )
                );
    }

    // ==========================================
    // CREATE PLAN
    // ==========================================

    public PolicyPlan createPlan(
            PolicyPlanRequest request) {

        validateRequest(request);

        String planCode =
                request.getPlanCode()
                        .trim()
                        .toUpperCase();

        // Duplicate Plan Code check
        if (policyPlanRepository
                .existsByPlanCode(planCode)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Plan code already exists. Please use a different Plan Code."
            );
        }

        PolicyPlan plan =
                new PolicyPlan();

        plan.setPlanCode(planCode);

        plan.setName(
                request.getName().trim()
        );

        plan.setDescription(
                request.getDescription().trim()
        );

        plan.setPremium(
                request.getPremium()
        );

        plan.setCoverageAmount(
                request.getCoverageAmount()
        );

        plan.setDurationYears(
                request.getDurationYears()
        );

        // IMPORTANT:
        // Always create mutable ArrayList
        plan.setBenefits(
                new ArrayList<>(
                        cleanList(
                                request.getBenefits()
                        )
                )
        );

        plan.setExclusions(
                new ArrayList<>(
                        cleanList(
                                request.getExclusions()
                        )
                )
        );

        plan.setStatus(
                convertStatus(
                        request.getStatus()
                )
        );

        return policyPlanRepository.save(plan);
    }

    // ==========================================
    // UPDATE PLAN
    // ==========================================

    public PolicyPlan updatePlan(
            Long id,
            PolicyPlanRequest request) {

        if (id == null || id <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid policy plan ID."
            );
        }

        validateRequest(request);

        PolicyPlan plan =
                policyPlanRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Policy plan not found."
                                )
                        );

        String planCode =
                request.getPlanCode()
                        .trim()
                        .toUpperCase();

        // ==========================================
        // DUPLICATE PLAN CODE CHECK
        // ==========================================

        policyPlanRepository
                .findByPlanCode(planCode)
                .ifPresent(existingPlan -> {

                    if (!existingPlan
                            .getId()
                            .equals(id)) {

                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Plan code already exists. Please use a different Plan Code."
                        );
                    }
                });

        // ==========================================
        // UPDATE BASIC DETAILS
        // ==========================================

        plan.setPlanCode(planCode);

        plan.setName(
                request.getName().trim()
        );

        plan.setDescription(
                request.getDescription().trim()
        );

        plan.setPremium(
                request.getPremium()
        );

        plan.setCoverageAmount(
                request.getCoverageAmount()
        );

        plan.setDurationYears(
                request.getDurationYears()
        );

        // ==========================================
        // IMPORTANT FIX
        // ==========================================
        //
        // DO NOT directly do:
        //
        // plan.setBenefits(request.getBenefits());
        //
        // because Hibernate may receive an immutable
        // collection and fail with:
        //
        // UnsupportedOperationException
        //
        // Instead, update the existing mutable list.
        // ==========================================

        List<String> benefits =
                cleanList(
                        request.getBenefits()
                );

        if (plan.getBenefits() == null) {

            plan.setBenefits(
                    new ArrayList<>()
            );
        }

        plan.getBenefits().clear();

        plan.getBenefits().addAll(
                benefits
        );

        // ==========================================
        // UPDATE EXCLUSIONS
        // ==========================================

        List<String> exclusions =
                cleanList(
                        request.getExclusions()
                );

        if (plan.getExclusions() == null) {

            plan.setExclusions(
                    new ArrayList<>()
            );
        }

        plan.getExclusions().clear();

        plan.getExclusions().addAll(
                exclusions
        );

        // ==========================================
        // UPDATE STATUS
        // ==========================================

        plan.setStatus(
                convertStatus(
                        request.getStatus()
                )
        );

        return policyPlanRepository.save(plan);
    }

    // ==========================================
    // DELETE PLAN
    // ==========================================

    public void deletePlan(Long id) {

        if (id == null || id <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid policy plan ID."
            );
        }

        PolicyPlan plan =
                policyPlanRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Policy plan not found."
                                )
                        );

        policyPlanRepository.delete(plan);
    }

    // ==========================================
    // VALIDATE REQUEST
    // ==========================================

    private void validateRequest(
            PolicyPlanRequest request) {

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Policy plan data is required."
            );
        }

        // Plan Code
        if (request.getPlanCode() == null ||
                request.getPlanCode().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Plan Code is required."
            );
        }

        // Plan Name
        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Plan Name is required."
            );
        }

        // Description
        if (request.getDescription() == null ||
                request.getDescription().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Description is required."
            );
        }

        // Premium
        if (request.getPremium() == null ||
                !Double.isFinite(
                        request.getPremium()
                ) ||
                request.getPremium() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Premium amount must be greater than zero."
            );
        }

        // Coverage
        if (request.getCoverageAmount() == null ||
                !Double.isFinite(
                        request.getCoverageAmount()
                ) ||
                request.getCoverageAmount() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Coverage amount must be greater than zero."
            );
        }

        // Duration
        if (request.getDurationYears() == null ||
                request.getDurationYears() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duration must be greater than zero."
            );
        }

        // Benefits
        List<String> benefits =
                cleanList(
                        request.getBenefits()
                );

        if (benefits.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one benefit is required."
            );
        }

        // Status
        if (request.getStatus() == null ||
                request.getStatus().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Policy plan status is required."
            );
        }

        String status =
                request.getStatus()
                        .trim()
                        .toUpperCase();

        if (!status.equals("ACTIVE") &&
                !status.equals("INACTIVE")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid policy plan status."
            );
        }
    }

    // ==========================================
    // CLEAN LIST
    // ==========================================

    private List<String> cleanList(
            List<String> values) {

        if (values == null) {

            return new ArrayList<>();
        }

        List<String> cleaned =
                new ArrayList<>();

        for (String value : values) {

            if (value != null) {

                String trimmed =
                        value.trim();

                if (!trimmed.isEmpty()) {

                    cleaned.add(trimmed);
                }
            }
        }

        return cleaned;
    }

    // ==========================================
    // CONVERT STATUS
    // ==========================================

    private PolicyPlanStatus convertStatus(
            String status) {

        if (status == null ||
                status.trim().isEmpty()) {

            return PolicyPlanStatus.ACTIVE;
        }

        String normalized =
                status.trim()
                        .toUpperCase();

        if (normalized.equals("ACTIVE")) {

            return PolicyPlanStatus.ACTIVE;
        }

        if (normalized.equals("INACTIVE")) {

            return PolicyPlanStatus.INACTIVE;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid policy plan status."
        );
    }
}