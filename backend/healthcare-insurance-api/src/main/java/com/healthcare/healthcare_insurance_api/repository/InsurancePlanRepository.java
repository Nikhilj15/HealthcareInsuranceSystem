package com.healthcare.healthcare_insurance_api.repository;

import com.healthcare.healthcare_insurance_api.entity.InsurancePlan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsurancePlanRepository
        extends JpaRepository<InsurancePlan, Long> {

    List<InsurancePlan> findByStatus(String status);

    Optional<InsurancePlan> findByPlanCode(String planCode);

    boolean existsByPlanCode(String planCode);
}