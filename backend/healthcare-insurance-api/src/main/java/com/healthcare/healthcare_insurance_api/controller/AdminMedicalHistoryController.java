
package com.healthcare.healthcare_insurance_api.controller;

import com.healthcare.healthcare_insurance_api.dto.MedicalHistoryRequest;
import com.healthcare.healthcare_insurance_api.dto.MedicalHistoryResponse;
import com.healthcare.healthcare_insurance_api.service.MedicalHistoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;


    public AdminMedicalHistoryController(
            MedicalHistoryService medicalHistoryService) {

        this.medicalHistoryService =
                medicalHistoryService;
    }


    // =========================================================
    // GET MEDICAL HISTORY
    // =========================================================

    @GetMapping(
            "/customers/{customerId}/medical-history"
    )
    public ResponseEntity<List<MedicalHistoryResponse>>
    getMedicalHistory(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                medicalHistoryService
                        .getCustomerMedicalHistory(
                                customerId
                        )
        );
    }


    // =========================================================
    // ADD MEDICAL HISTORY
    // =========================================================

    @PostMapping(
            "/customers/{customerId}/medical-history"
    )
    public ResponseEntity<MedicalHistoryResponse>
    addMedicalHistory(
            @PathVariable Long customerId,
            @RequestBody MedicalHistoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        medicalHistoryService
                                .addMedicalHistory(
                                        customerId,
                                        request
                                )
                );
    }


    // =========================================================
    // UPDATE MEDICAL HISTORY
    // =========================================================

    @PutMapping(
            "/medical-history/{id}"
    )
    public ResponseEntity<MedicalHistoryResponse>
    updateMedicalHistory(
            @PathVariable Long id,
            @RequestBody MedicalHistoryRequest request) {

        return ResponseEntity.ok(
                medicalHistoryService
                        .updateMedicalHistory(
                                id,
                                request
                        )
        );
    }


    // =========================================================
    // DELETE MEDICAL HISTORY
    // =========================================================

    @DeleteMapping(
            "/medical-history/{id}"
    )
    public ResponseEntity<Void>
    deleteMedicalHistory(
            @PathVariable Long id) {

        medicalHistoryService
                .deleteMedicalHistory(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}