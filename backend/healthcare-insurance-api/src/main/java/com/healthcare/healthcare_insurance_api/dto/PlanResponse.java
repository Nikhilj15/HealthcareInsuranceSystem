package com.healthcare.healthcare_insurance_api.dto;

import java.math.BigDecimal;

public class PlanResponse {

    private Long id;

    private String planCode;

    private String name;

    private String description;

    private BigDecimal premium;

    private BigDecimal coverageAmount;

    private Integer durationYears;

    private String benefits;

    private String status;

    public PlanResponse() {
    }

    public PlanResponse(
            Long id,
            String planCode,
            String name,
            String description,
            BigDecimal premium,
            BigDecimal coverageAmount,
            Integer durationYears,
            String benefits,
            String status) {

        this.id = id;
        this.planCode = planCode;
        this.name = name;
        this.description = description;
        this.premium = premium;
        this.coverageAmount = coverageAmount;
        this.durationYears = durationYears;
        this.benefits = benefits;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public Integer getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(Integer durationYears) {
        this.durationYears = durationYears;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}