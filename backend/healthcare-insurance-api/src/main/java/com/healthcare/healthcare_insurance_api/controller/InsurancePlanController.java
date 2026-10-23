package com.healthcare.healthcare_insurance_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.healthcare.healthcare_insurance_api.dto.PlanResponse;
import com.healthcare.healthcare_insurance_api.service.InsurancePlanService;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "http://localhost:4200")
public class InsurancePlanController {

    private final InsurancePlanService planService;

    public InsurancePlanController(
            InsurancePlanService planService) {

        this.planService = planService;
    }

    // GET ALL ACTIVE PLANS

    @GetMapping
    public ResponseEntity<List<PlanResponse>> getActivePlans() {

        return ResponseEntity.ok(
                planService.getActivePlans()
        );
    }

    // GET PLAN BY ID

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> getPlanById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                planService.getPlanById(id)
        );
    }
}