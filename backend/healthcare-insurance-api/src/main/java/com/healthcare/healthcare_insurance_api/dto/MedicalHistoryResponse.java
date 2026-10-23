package com.healthcare.healthcare_insurance_api.dto;

import java.time.LocalDate;

public class MedicalHistoryResponse {

    private Long id;

    private Long customerId;

    private String condition;

    private LocalDate diagnosisDate;

    private String treatment;

    private String doctorName;

    private String hospitalName;

    private String status;

    private String notes;


    public MedicalHistoryResponse() {
    }


    public MedicalHistoryResponse(
            Long id,
            Long customerId,
            String condition,
            LocalDate diagnosisDate,
            String treatment,
            String doctorName,
            String hospitalName,
            String status,
            String notes) {

        this.id = id;
        this.customerId = customerId;
        this.condition = condition;
        this.diagnosisDate = diagnosisDate;
        this.treatment = treatment;
        this.doctorName = doctorName;
        this.hospitalName = hospitalName;
        this.status = status;
        this.notes = notes;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}