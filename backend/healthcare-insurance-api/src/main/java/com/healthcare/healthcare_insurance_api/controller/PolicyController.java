package com.healthcare.healthcare_insurance_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.healthcare_insurance_api.dto.PolicyPurchaseRequest;
import com.healthcare.healthcare_insurance_api.dto.PolicyRejectRequest;
import com.healthcare.healthcare_insurance_api.entity.Policy;
import com.healthcare.healthcare_insurance_api.service.PolicyService;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "http://localhost:4200")
public class PolicyController {

    private final PolicyService policyService;


    public PolicyController(
            PolicyService policyService) {

        this.policyService =
                policyService;
    }


    // =========================================================
    // ADMIN - GET ALL POLICIES
    // SEARCH + FILTER
    // =========================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Policy>> getAllPolicies(

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String search,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String policyNumber,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String customerName,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String planName,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String status) {

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "ADMIN - GET POLICIES"
        );

        System.out.println(
                "Search       : " + search
        );

        System.out.println(
                "Policy ID    : " + policyNumber
        );

        System.out.println(
                "Customer     : " + customerName
        );

        System.out.println(
                "Plan         : " + planName
        );

        System.out.println(
                "Status       : " + status
        );

        System.out.println(
                "=========================================="
        );


        return ResponseEntity.ok(
                policyService.getAdminPolicies(
                        search,
                        policyNumber,
                        customerName,
                        planName,
                        status
                )
        );
    }


    // =========================================================
    // USER / ADMIN - GET POLICY BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Policy> getPolicyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                policyService.getPolicyById(id)
        );
    }


    // =========================================================
    // USER - PURCHASE POLICY
    // =========================================================

    @PostMapping("/purchase")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Policy> purchasePolicy(
            @RequestBody PolicyPurchaseRequest request,
            Authentication authentication) {

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "PURCHASE POLICY REQUEST"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Authenticated User : "
                        + authentication.getName()
        );

        System.out.println(
                "Plan ID : "
                        + request.getPlanId()
        );


        Policy policy =
                policyService.purchasePolicy(
                        authentication.getName(),
                        request
                );


        System.out.println(
                "Policy Created : "
                        + policy.getPolicyNumber()
        );

        System.out.println(
                "Policy Status : "
                        + policy.getStatus()
        );

        System.out.println(
                "=========================================="
        );

        System.out.println();


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(policy);
    }


    // =========================================================
    // USER - GET USER POLICIES
    // =========================================================

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Policy>> getUserPolicies(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                policyService.getUserPolicies(userId)
        );
    }


    // =========================================================
    // ADMIN - APPROVE POLICY
    // =========================================================

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Policy> approvePolicy(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                policyService.approvePolicy(id)
        );
    }


    // =========================================================
    // ADMIN - REJECT POLICY
    // =========================================================

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Policy> rejectPolicy(
            @PathVariable Long id,
            @RequestBody PolicyRejectRequest request) {

        return ResponseEntity.ok(
                policyService.rejectPolicy(
                        id,
                        request
                )
        );
    }

}

