package com.healthcare.healthcare_insurance_api.service;

import org.springframework.stereotype.Service;

import com.healthcare.healthcare_insurance_api.dto.DashboardStats;
import com.healthcare.healthcare_insurance_api.enums.ClaimStatus;
import com.healthcare.healthcare_insurance_api.enums.PolicyStatus;
import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.repository.ClaimRepository;
import com.healthcare.healthcare_insurance_api.repository.PolicyRepository;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

@Service
public class DashboardService {

    private final UserRepository userRepository;

    private final PolicyRepository policyRepository;

    private final ClaimRepository claimRepository;

    public DashboardService(
            UserRepository userRepository,
            PolicyRepository policyRepository,
            ClaimRepository claimRepository) {

        this.userRepository = userRepository;

        this.policyRepository = policyRepository;

        this.claimRepository = claimRepository;
    }

    public DashboardStats getDashboardStats() {

        long totalCustomers =
                userRepository.countByRole(Role.USER);

        long totalPolicies =
                policyRepository.count();

        long activePolicies =
                policyRepository.countByStatus(
                        PolicyStatus.ACTIVE
                );

        long pendingClaims =
                claimRepository.countByStatus(
                        ClaimStatus.PENDING
                );

        long approvedClaims =
                claimRepository.countByStatus(
                        ClaimStatus.APPROVED
                );

        long rejectedClaims =
                claimRepository.countByStatus(
                        ClaimStatus.REJECTED
                );

        return new DashboardStats(
                totalCustomers,
                totalPolicies,
                activePolicies,
                pendingClaims,
                approvedClaims,
                rejectedClaims
        );
    }
}