package com.healthcare.healthcare_insurance_api.service;

import com.healthcare.healthcare_insurance_api.dto.PaymentRequest;
import com.healthcare.healthcare_insurance_api.dto.PaymentResponse;
import com.healthcare.healthcare_insurance_api.entity.Payment;
import com.healthcare.healthcare_insurance_api.entity.Policy;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.PaymentStatus;
import com.healthcare.healthcare_insurance_api.enums.PolicyStatus;
import com.healthcare.healthcare_insurance_api.repository.PaymentRepository;
import com.healthcare.healthcare_insurance_api.repository.PolicyRepository;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            PolicyRepository policyRepository) {

        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
    }

    // USER - Make Payment
    public PaymentResponse makePayment(
            String userEmail,
            PaymentRequest request) {

        if (request == null) {
            throw new RuntimeException("Payment request cannot be null");
        }

        if (request.getPolicyId() == null) {
            throw new RuntimeException("Policy ID is required");
        }

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Payment amount must be greater than zero");
        }

        if (request.getPaymentMethod() == null ||
                request.getPaymentMethod().trim().isEmpty()) {

            throw new RuntimeException("Payment method is required");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() ->
                        new RuntimeException("Policy not found"));

        // Ownership check
        if (!policy.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to make payment for this policy");
        }

        // Policy must be ACTIVE
        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new RuntimeException(
                    "Payment can only be made for an active policy");
        }

        // Payment amount must match policy premium
        if (request.getAmount().compareTo(policy.getPremiumAmount()) != 0) {
            throw new RuntimeException(
                    "Payment amount must match the policy premium");
        }

        Payment payment = new Payment();

        payment.setTransactionId(generateTransactionId());
        payment.setPolicy(policy);
        payment.setUser(user);
        payment.setAmount(request.getAmount());

        // Demo payment system:
        // payment is considered successful immediately
        payment.setStatus(PaymentStatus.SUCCESS);

        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(
                request.getPaymentMethod().trim().toUpperCase());

        payment.setDescription(request.getDescription());

        Payment savedPayment = paymentRepository.save(payment);

        return convertToResponse(savedPayment);
    }

    // USER - Get My Payments
    public List<PaymentResponse> getMyPayments(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return paymentRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // USER - Get My Payment By ID
    public PaymentResponse getMyPaymentById(
            Long paymentId,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to view this payment");
        }

        return convertToResponse(payment);
    }

    // ADMIN - Get All Payments
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ADMIN - Get Payment By ID
    public PaymentResponse getPaymentById(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        return convertToResponse(payment);
    }

    private String generateTransactionId() {

        String transactionId;

        do {
            transactionId = "TXN-" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();

        } while (paymentRepository
                .findByTransactionId(transactionId)
                .isPresent());

        return transactionId;
    }

    private PaymentResponse convertToResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setTransactionId(payment.getTransactionId());

        response.setPolicyId(
                payment.getPolicy().getId());

        response.setPolicyNumber(
                payment.getPolicy().getPolicyNumber());

        response.setUserId(
                payment.getUser().getId());

        response.setUserName(
                payment.getUser().getFullName());

        response.setUserEmail(
                payment.getUser().getEmail());

        response.setAmount(
                payment.getAmount());

        response.setStatus(
                payment.getStatus());

        response.setPaymentDate(
                payment.getPaymentDate());

        response.setPaymentMethod(
                payment.getPaymentMethod());

        response.setDescription(
                payment.getDescription());

        return response;
    }
}