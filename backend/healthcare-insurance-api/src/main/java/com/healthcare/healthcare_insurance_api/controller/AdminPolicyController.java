package com.healthcare.healthcare_insurance_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.healthcare_insurance_api.dto.PolicyRejectRequest;
import com.healthcare.healthcare_insurance_api.entity.Policy;
import com.healthcare.healthcare_insurance_api.service.PolicyService;


@RestController
@RequestMapping("/api/admin/policies")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPolicyController {


    private final PolicyService policyService;


    public AdminPolicyController(
            PolicyService policyService) {

        this.policyService = policyService;

    }


    /*
     * ============================================================
     * GET ALL ADMIN POLICIES
     * ============================================================
     *
     * Supports:
     *
     * /api/admin/policies
     *
     * /api/admin/policies?search=health
     *
     * /api/admin/policies?policyNumber=POL-123
     *
     * /api/admin/policies?customerName=Sanika
     *
     * /api/admin/policies?planName=Health
     *
     * /api/admin/policies?status=PENDING
     *
     */


    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies(

            @RequestParam(
                    required = false
            )
            String search,

            @RequestParam(
                    required = false
            )
            String policyNumber,

            @RequestParam(
                    required = false
            )
            String customerName,

            @RequestParam(
                    required = false
            )
            String planName,

            @RequestParam(
                    required = false
            )
            String status) {


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


    /*
     * ============================================================
     * GET SINGLE POLICY
     * ============================================================
     */

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(

            @PathVariable Long id) {


        return ResponseEntity.ok(
                policyService.getPolicyById(id)
        );

    }


    /*
     * ============================================================
     * APPROVE POLICY
     * ============================================================
     */

    @PutMapping("/{id}/approve")
    public ResponseEntity<Policy> approvePolicy(

            @PathVariable Long id) {


        return ResponseEntity.ok(
                policyService.approvePolicy(id)
        );

    }


    /*
     * ============================================================
     * REJECT POLICY
     * ============================================================
     */

    @PutMapping("/{id}/reject")
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