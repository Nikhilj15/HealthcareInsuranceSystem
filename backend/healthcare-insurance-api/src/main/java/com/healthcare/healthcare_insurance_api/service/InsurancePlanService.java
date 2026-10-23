package com.healthcare.healthcare_insurance_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.healthcare.healthcare_insurance_api.dto.PlanRequest;
import com.healthcare.healthcare_insurance_api.dto.PlanResponse;
import com.healthcare.healthcare_insurance_api.entity.InsurancePlan;
import com.healthcare.healthcare_insurance_api.repository.InsurancePlanRepository;

@Service
public class InsurancePlanService {

    private final InsurancePlanRepository planRepository;

    public InsurancePlanService(
            InsurancePlanRepository planRepository) {

        this.planRepository = planRepository;
    }

    // =========================
    // GET ACTIVE PLANS
    // =========================

    public List<PlanResponse> getActivePlans() {

        return planRepository
                .findByStatus("ACTIVE")
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================
    // GET PLAN BY ID
    // =========================

    public PlanResponse getPlanById(Long id) {

        InsurancePlan plan = planRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Insurance plan not found with id: " + id
                        )
                );

        return convertToResponse(plan);
    }

    // =========================
    // CREATE PLAN
    // =========================

    public PlanResponse createPlan(PlanRequest request) {

        // Validate required fields
        validatePlanRequest(request);

        String planCode =
                request.getPlanCode().trim().toUpperCase();

        // Check duplicate plan code
        if (planRepository.existsByPlanCode(planCode)) {

            throw new RuntimeException(
                    "Plan code already exists: " + planCode
            );
        }

        InsurancePlan plan = new InsurancePlan();

        plan.setPlanCode(planCode);

        plan.setName(request.getName().trim());

        plan.setDescription(request.getDescription());

        plan.setPremium(request.getPremium());

        plan.setCoverageAmount(
                request.getCoverageAmount()
        );

        plan.setDurationYears(
                request.getDurationYears()
        );

        plan.setBenefits(request.getBenefits());

        // Default status
        if (request.getStatus() == null ||
                request.getStatus().trim().isEmpty()) {

            plan.setStatus("ACTIVE");

        } else {

            plan.setStatus(
                    request.getStatus()
                            .trim()
                            .toUpperCase()
            );
        }

        InsurancePlan savedPlan =
                planRepository.save(plan);

        return convertToResponse(savedPlan);
    }

    // =========================
    // UPDATE PLAN
    // =========================

    public PlanResponse updatePlan(
            Long id,
            PlanRequest request) {

        validatePlanRequest(request);

        InsurancePlan plan = planRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Insurance plan not found with id: " + id
                        )
                );

        plan.setName(request.getName().trim());

        plan.setDescription(request.getDescription());

        plan.setPremium(request.getPremium());

        plan.setCoverageAmount(
                request.getCoverageAmount()
        );

        plan.setDurationYears(
                request.getDurationYears()
        );

        plan.setBenefits(request.getBenefits());

        if (request.getStatus() != null &&
                !request.getStatus().trim().isEmpty()) {

            plan.setStatus(
                    request.getStatus()
                            .trim()
                            .toUpperCase()
            );
        }

        InsurancePlan updatedPlan =
                planRepository.save(plan);

        return convertToResponse(updatedPlan);
    }

    // =========================
    // DEACTIVATE PLAN
    // =========================

    public PlanResponse deactivatePlan(Long id) {

        InsurancePlan plan = planRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Insurance plan not found with id: " + id
                        )
                );

        plan.setStatus("INACTIVE");

        InsurancePlan updatedPlan =
                planRepository.save(plan);

        return convertToResponse(updatedPlan);
    }

    // =========================
    // VALIDATION
    // =========================

    private void validatePlanRequest(
            PlanRequest request) {

        if (request == null) {
            throw new RuntimeException(
                    "Plan request cannot be null"
            );
        }

        if (request.getPlanCode() == null ||
                request.getPlanCode().trim().isEmpty()) {

            throw new RuntimeException(
                    "Plan code is required"
            );
        }

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Plan name is required"
            );
        }

        if (request.getPremium() == null) {

            throw new RuntimeException(
                    "Premium is required"
            );
        }

        if (request.getCoverageAmount() == null) {

            throw new RuntimeException(
                    "Coverage amount is required"
            );
        }

        if (request.getDurationYears() == null) {

            throw new RuntimeException(
                    "Duration years is required"
            );
        }

        if (request.getPremium().signum() < 0) {

            throw new RuntimeException(
                    "Premium cannot be negative"
            );
        }

        if (request.getCoverageAmount().signum() < 0) {

            throw new RuntimeException(
                    "Coverage amount cannot be negative"
            );
        }

        if (request.getDurationYears() <= 0) {

            throw new RuntimeException(
                    "Duration years must be greater than zero"
            );
        }
    }

    // =========================
    // ENTITY → RESPONSE
    // =========================

    private PlanResponse convertToResponse(
            InsurancePlan plan) {

        return new PlanResponse(
                plan.getId(),
                plan.getPlanCode(),
                plan.getName(),
                plan.getDescription(),
                plan.getPremium(),
                plan.getCoverageAmount(),
                plan.getDurationYears(),
                plan.getBenefits(),
                plan.getStatus()
        );
    }
}