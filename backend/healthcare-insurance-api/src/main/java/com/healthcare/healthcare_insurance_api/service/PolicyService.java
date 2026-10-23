package com.healthcare.healthcare_insurance_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import com.healthcare.healthcare_insurance_api.dto.PolicyPurchaseRequest;
import com.healthcare.healthcare_insurance_api.dto.PolicyRejectRequest;
import com.healthcare.healthcare_insurance_api.entity.Policy;
import com.healthcare.healthcare_insurance_api.entity.PolicyPlan;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.PolicyPlanStatus;
import com.healthcare.healthcare_insurance_api.enums.PolicyStatus;
import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.repository.PolicyPlanRepository;
import com.healthcare.healthcare_insurance_api.repository.PolicyRepository;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

@Service
@Transactional
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyPlanRepository policyPlanRepository;
    private final UserRepository userRepository;

    public PolicyService(
            PolicyRepository policyRepository,
            PolicyPlanRepository policyPlanRepository,
            UserRepository userRepository) {

        this.policyRepository = policyRepository;
        this.policyPlanRepository = policyPlanRepository;
        this.userRepository = userRepository;
    }

    // ============================================================
    // GET ALL POLICIES
    // ============================================================

    public List<Policy> getAllPolicies() {

        return policyRepository.findAll();
    }

    // ============================================================
    // GET POLICY BY ID
    // ============================================================

    public Policy getPolicyById(Long id) {

        return policyRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Policy not found with ID: " + id
                        )
                );
    }

    // ============================================================
    // PURCHASE POLICY - USER SIDE
    // ============================================================

    public Policy purchasePolicy(
            String userEmail,
            PolicyPurchaseRequest request) {

        // --------------------------------------------------------
        // Validate request
        // --------------------------------------------------------

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Purchase request cannot be null"
            );
        }

        if (request.getPlanId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Plan ID is required"
            );
        }

        // --------------------------------------------------------
        // Find user
        // --------------------------------------------------------

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        // --------------------------------------------------------
        // Only USER can purchase
        // --------------------------------------------------------

        if (user.getRole() != Role.USER) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only users can purchase insurance policies"
            );
        }

        // --------------------------------------------------------
        // Find policy plan
        // --------------------------------------------------------

        PolicyPlan plan = policyPlanRepository
                .findById(request.getPlanId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Insurance plan not found"
                        )
                );

        // --------------------------------------------------------
        // Plan must be ACTIVE
        // --------------------------------------------------------

        if (plan.getStatus() != PolicyPlanStatus.ACTIVE) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This policy plan is inactive and cannot be purchased"
            );
        }

        // --------------------------------------------------------
        // Validate premium
        // --------------------------------------------------------

        if (plan.getPremium() == null ||
                plan.getPremium() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid premium amount"
            );
        }

        // --------------------------------------------------------
        // Validate coverage amount
        // --------------------------------------------------------

        if (plan.getCoverageAmount() == null ||
                plan.getCoverageAmount() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid coverage amount"
            );
        }

        // --------------------------------------------------------
        // Validate duration
        // --------------------------------------------------------

        if (plan.getDurationYears() == null ||
                plan.getDurationYears() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid policy duration"
            );
        }

        // --------------------------------------------------------
        // Prevent duplicate purchase
        // --------------------------------------------------------

        boolean alreadyPurchased =
                policyRepository
                        .existsByUserIdAndPlanIdAndStatusIn(
                                user.getId(),
                                plan.getId(),
                                List.of(
                                        PolicyStatus.PENDING,
                                        PolicyStatus.APPROVED,
                                        PolicyStatus.ACTIVE
                                )
                        );

        if (alreadyPurchased) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You already have this policy plan"
            );
        }

        // --------------------------------------------------------
        // Create Policy
        // --------------------------------------------------------

        Policy policy = new Policy();

        policy.setPolicyNumber(
                generatePolicyNumber()
        );

        policy.setUser(user);

        policy.setPlan(plan);

        // --------------------------------------------------------
        // IMPORTANT:
        // Policy expects BigDecimal.
        // PolicyPlan currently returns Double.
        // --------------------------------------------------------

        policy.setPremiumAmount(
                BigDecimal.valueOf(
                        plan.getPremium()
                )
        );

        policy.setCoverageAmount(
                BigDecimal.valueOf(
                        plan.getCoverageAmount()
                )
        );

        // --------------------------------------------------------
        // Start and End Date
        // --------------------------------------------------------

        LocalDate startDate =
                LocalDate.now();

        LocalDate endDate =
                startDate.plusYears(
                        plan.getDurationYears()
                );

        policy.setStartDate(startDate);

        policy.setEndDate(endDate);

        // --------------------------------------------------------
        // Initial Status
        // --------------------------------------------------------

        policy.setStatus(
                PolicyStatus.PENDING
        );

        policy.setAppliedAt(
                LocalDateTime.now()
        );

        policy.setApprovedAt(null);

        policy.setRejectedAt(null);

        policy.setRejectionReason(null);

        // --------------------------------------------------------
        // Save
        // --------------------------------------------------------

        return policyRepository.save(policy);
    }

    // ============================================================
    // GET USER POLICIES
    // ============================================================

    public List<Policy> getUserPolicies(Long userId) {

        if (!userRepository.existsById(userId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found with ID: " + userId
            );
        }

        return policyRepository.findByUserId(userId);
    }

    // ============================================================
    // ADMIN - SEARCH + FILTER
    // ============================================================

    public List<Policy> getAdminPolicies(
            String search,
            String policyNumber,
            String customerName,
            String planName,
            String status) {

        Specification<Policy> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        // ========================================================
        // GLOBAL SEARCH
        // ========================================================

        if (search != null &&
                !search.trim().isEmpty()) {

            String searchValue =
                    "%" +
                    search.trim().toLowerCase() +
                    "%";

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) -> {

                                Join<Policy, User> user =
                                        root.join(
                                                "user",
                                                JoinType.LEFT
                                        );

                                Join<Policy, PolicyPlan> plan =
                                        root.join(
                                                "plan",
                                                JoinType.LEFT
                                        );

                                return criteriaBuilder.or(

                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get(
                                                                "policyNumber"
                                                        )
                                                ),
                                                searchValue
                                        ),

                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        user.get(
                                                                "fullName"
                                                        )
                                                ),
                                                searchValue
                                        ),

                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        plan.get(
                                                                "name"
                                                        )
                                                ),
                                                searchValue
                                        ),

                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        plan.get(
                                                                "planCode"
                                                        )
                                                ),
                                                searchValue
                                        )
                                );
                            }
                    );
        }

        // ========================================================
        // POLICY NUMBER FILTER
        // ========================================================

        if (policyNumber != null &&
                !policyNumber.trim().isEmpty()) {

            String value =
                    "%" +
                    policyNumber.trim().toLowerCase() +
                    "%";

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get(
                                                            "policyNumber"
                                                    )
                                            ),
                                            value
                                    )
                    );
        }

        // ========================================================
        // CUSTOMER NAME FILTER
        // ========================================================

        if (customerName != null &&
                !customerName.trim().isEmpty()) {

            String value =
                    "%" +
                    customerName.trim().toLowerCase() +
                    "%";

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) -> {

                                Join<Policy, User> user =
                                        root.join(
                                                "user",
                                                JoinType.LEFT
                                        );

                                return criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                user.get(
                                                        "fullName"
                                                )
                                        ),
                                        value
                                );
                            }
                    );
        }

        // ========================================================
        // PLAN NAME FILTER
        // ========================================================

        if (planName != null &&
                !planName.trim().isEmpty()) {

            String value =
                    "%" +
                    planName.trim().toLowerCase() +
                    "%";

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) -> {

                                Join<Policy, PolicyPlan> plan =
                                        root.join(
                                                "plan",
                                                JoinType.LEFT
                                        );

                                return criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                plan.get(
                                                        "name"
                                                )
                                        ),
                                        value
                                );
                            }
                    );
        }

        // ========================================================
        // STATUS FILTER
        // ========================================================

        if (status != null &&
                !status.trim().isEmpty()) {

            final PolicyStatus policyStatus;

            try {

                policyStatus =
                        PolicyStatus.valueOf(
                                status.trim().toUpperCase()
                        );

            } catch (IllegalArgumentException exception) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid policy status: " + status
                );
            }

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("status"),
                                            policyStatus
                                    )
                    );
        }

        // ========================================================
        // Execute Search / Filter
        // ========================================================

        return policyRepository.findAll(
                specification
        );
    }

    // ============================================================
    // ADMIN - APPROVE POLICY
    // ============================================================

    public Policy approvePolicy(Long id) {

        Policy policy =
                getPolicyById(id);

        if (policy.getStatus() != PolicyStatus.PENDING) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending policies can be approved"
            );
        }

        policy.setStatus(
                PolicyStatus.APPROVED
        );

        policy.setApprovedAt(
                LocalDateTime.now()
        );

        policy.setRejectedAt(null);

        policy.setRejectionReason(null);

        return policyRepository.save(policy);
    }

    // ============================================================
    // ADMIN - REJECT POLICY
    // ============================================================

    public Policy rejectPolicy(
            Long id,
            PolicyRejectRequest request) {

        Policy policy =
                getPolicyById(id);

        if (policy.getStatus() != PolicyStatus.PENDING) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending policies can be rejected"
            );
        }

        // --------------------------------------------------------
        // Validate request
        // --------------------------------------------------------

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rejection reason is required"
            );
        }

        String reason =
                request.getReason();

        if (reason == null ||
                reason.trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rejection reason is required"
            );
        }

        if (reason.trim().length() > 500) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rejection reason cannot exceed 500 characters"
            );
        }

        // --------------------------------------------------------
        // Update policy
        // --------------------------------------------------------

        policy.setStatus(
                PolicyStatus.REJECTED
        );

        policy.setRejectedAt(
                LocalDateTime.now()
        );

        policy.setRejectionReason(
                reason.trim()
        );

        policy.setApprovedAt(null);

        return policyRepository.save(policy);
    }

    // ============================================================
    // GENERATE POLICY NUMBER
    // ============================================================

    private String generatePolicyNumber() {

        while (true) {

            String randomPart =
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 8)
                            .toUpperCase();

            final String generatedPolicyNumber =
                    "POL-" + randomPart;

            boolean exists =
                    policyRepository
                            .findAll()
                            .stream()
                            .anyMatch(
                                    policy ->
                                            generatedPolicyNumber.equals(
                                                    policy.getPolicyNumber()
                                            )
                            );

            if (!exists) {

                return generatedPolicyNumber;
            }
        }
    }
}