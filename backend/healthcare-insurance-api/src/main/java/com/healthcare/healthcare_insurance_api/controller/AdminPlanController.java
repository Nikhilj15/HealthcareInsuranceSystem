package com.healthcare.healthcare_insurance_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.healthcare.healthcare_insurance_api.dto.PlanRequest;
import com.healthcare.healthcare_insurance_api.dto.PlanResponse;
import com.healthcare.healthcare_insurance_api.service.InsurancePlanService;

@RestController
@RequestMapping("/api/admin/plans")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlanController {

    private final InsurancePlanService planService;

    public AdminPlanController(
            InsurancePlanService planService) {

        this.planService = planService;
    }

    // CREATE PLAN

    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(
            @RequestBody PlanRequest request) {

        PlanResponse response =
                planService.createPlan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // UPDATE PLAN

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable Long id,
            @RequestBody PlanRequest request) {

        PlanResponse response =
                planService.updatePlan(id, request);

        return ResponseEntity.ok(response);
    }

    // DEACTIVATE PLAN

    @DeleteMapping("/{id}")
    public ResponseEntity<PlanResponse> deactivatePlan(
            @PathVariable Long id) {

        PlanResponse response =
                planService.deactivatePlan(id);

        return ResponseEntity.ok(response);
    }
}