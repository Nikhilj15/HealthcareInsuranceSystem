package com.healthcare.healthcare_insurance_api.repository;

import com.healthcare.healthcare_insurance_api.entity.Claim;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.ClaimStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository
        extends JpaRepository<Claim, Long> {

    List<Claim> findByUser(User user);

    Optional<Claim> findByClaimNumber(String claimNumber);

    List<Claim> findByStatus(ClaimStatus status);

    List<Claim> findByUserAndStatus(
            User user,
            ClaimStatus status
    );

    long countByStatus(ClaimStatus status);

    List<Claim> findTop10ByOrderByClaimDateDesc();
}