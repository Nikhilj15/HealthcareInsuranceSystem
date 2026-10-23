package com.healthcare.healthcare_insurance_api.controller;

import com.healthcare.healthcare_insurance_api.dto.PaymentRequest;
import com.healthcare.healthcare_insurance_api.dto.PaymentResponse;
import com.healthcare.healthcare_insurance_api.service.PaymentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Make Payment
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> makePayment(
            @RequestBody PaymentRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        PaymentResponse response =
                paymentService.makePayment(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // My Payments
    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                paymentService.getMyPayments(email)
        );
    }

    // My Payment By ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getMyPaymentById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                paymentService.getMyPaymentById(id, email)
        );
    }
}