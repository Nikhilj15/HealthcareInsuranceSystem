package com.healthcare.healthcare_insurance_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.healthcare.healthcare_insurance_api.entity.Policy;
import com.healthcare.healthcare_insurance_api.enums.PolicyStatus;


public interface PolicyRepository
        extends JpaRepository<Policy, Long>,
                JpaSpecificationExecutor<Policy> {


    List<Policy> findByUserId(Long userId);


    List<Policy> findByStatus(
            PolicyStatus status
    );


    long countByStatus(
            PolicyStatus status
    );


    List<Policy> findTop10ByOrderByAppliedAtDesc();


    boolean existsByUserIdAndPlanIdAndStatusIn(
            Long userId,
            Long planId,
            List<PolicyStatus> statuses
    );

}