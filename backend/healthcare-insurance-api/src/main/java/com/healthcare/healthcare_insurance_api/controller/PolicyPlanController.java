package com.healthcare.healthcare_insurance_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.healthcare_insurance_api.dto.PolicyPlanRequest;
import com.healthcare.healthcare_insurance_api.entity.PolicyPlan;
import com.healthcare.healthcare_insurance_api.service.PolicyPlanService;

@RestController
@RequestMapping("/api/policy-plans")
@CrossOrigin(origins = "http://localhost:4200")
public class PolicyPlanController {

    private final PolicyPlanService policyPlanService;

    public PolicyPlanController(
            PolicyPlanService policyPlanService) {

        this.policyPlanService =
                policyPlanService;
    }

    // -----------------------------------------
    // USER + ADMIN
    // -----------------------------------------

    @GetMapping
    public ResponseEntity<List<PolicyPlan>> getAllPlans() {

        return ResponseEntity.ok(
                policyPlanService.getAllPlans()
        );
    }

    // -----------------------------------------
    // ACTIVE PLANS
    // -----------------------------------------

    @GetMapping("/active")
    public ResponseEntity<List<PolicyPlan>> getActivePlans() {

        return ResponseEntity.ok(
                policyPlanService.getActivePlans()
        );
    }

    // -----------------------------------------
    // GET BY ID
    // -----------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<PolicyPlan> getPlanById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                policyPlanService.getPlanById(id)
        );
    }

    // -----------------------------------------
    // ADMIN CREATE
    // -----------------------------------------

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PolicyPlan> createPlan(
            @RequestBody PolicyPlanRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        policyPlanService.createPlan(
                                request
                        )
                );
    }

    // -----------------------------------------
    // ADMIN UPDATE
    // -----------------------------------------

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PolicyPlan> updatePlan(
            @PathVariable Long id,
            @RequestBody PolicyPlanRequest request) {

        return ResponseEntity.ok(
                policyPlanService.updatePlan(
                        id,
                        request
                )
        );
    }

    // -----------------------------------------
    // ADMIN DELETE
    // -----------------------------------------

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long id) {

        policyPlanService.deletePlan(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}