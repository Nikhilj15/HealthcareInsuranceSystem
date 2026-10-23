package com.healthcare.healthcare_insurance_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.healthcare_insurance_api.entity.PolicyPlan;
import com.healthcare.healthcare_insurance_api.enums.PolicyPlanStatus;

public interface PolicyPlanRepository
        extends JpaRepository<PolicyPlan, Long> {

    Optional<PolicyPlan> findByPlanCode(String planCode);

    List<PolicyPlan> findByStatus(
            PolicyPlanStatus status
    );

    long countByStatus(
            PolicyPlanStatus status
    );

    boolean existsByPlanCode(String planCode);
}