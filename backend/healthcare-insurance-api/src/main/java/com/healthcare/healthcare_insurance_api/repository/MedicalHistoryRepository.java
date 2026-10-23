package com.healthcare.healthcare_insurance_api.repository;

import com.healthcare.healthcare_insurance_api.entity.MedicalHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository
        extends JpaRepository<MedicalHistory, Long> {

    List<MedicalHistory> findByUserIdOrderByDiagnosisDateDesc(
            Long userId
    );
}