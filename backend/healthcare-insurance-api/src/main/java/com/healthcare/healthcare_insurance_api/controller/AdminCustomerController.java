package com.healthcare.healthcare_insurance_api.controller;

import com.healthcare.healthcare_insurance_api.dto.CustomerResponse;
import com.healthcare.healthcare_insurance_api.enums.UserStatus;
import com.healthcare.healthcare_insurance_api.service.AdminCustomerService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final AdminCustomerService adminCustomerService;


    public AdminCustomerController(
            AdminCustomerService adminCustomerService) {

        this.adminCustomerService =
                adminCustomerService;
    }


    // =========================================================
    // GET ALL CUSTOMERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<CustomerResponse>>
    getAllCustomers() {

        return ResponseEntity.ok(

                adminCustomerService.getAllCustomers()

        );
    }


    // =========================================================
    // GET CUSTOMER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse>
    getCustomerById(
            @PathVariable Long id) {

        return ResponseEntity.ok(

                adminCustomerService.getCustomerById(id)

        );
    }


    // =========================================================
    // UPDATE CUSTOMER STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerResponse>
    updateCustomerStatus(

            @PathVariable Long id,

            @RequestBody Map<String, String> request) {


        String statusValue =
                request.get("status");


        if (statusValue == null ||
                statusValue.isBlank()) {

            throw new IllegalArgumentException(
                    "Status is required"
            );
        }


        UserStatus newStatus;

        try {

            newStatus =
                    UserStatus.valueOf(
                            statusValue.toUpperCase()
                    );

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Invalid customer status"
            );
        }


        CustomerResponse response =
                adminCustomerService.updateCustomerStatus(
                        id,
                        newStatus );
    

        return ResponseEntity.ok(response);
    }
}