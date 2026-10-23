package com.healthcare.healthcare_insurance_api.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import com.healthcare.healthcare_insurance_api.enums.PolicyPlanStatus;

@Entity
@Table(name = "policy_plans")
public class PolicyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String planCode;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double premium;

    @Column(nullable = false)
    private Double coverageAmount;

    @Column(nullable = false)
    private Integer durationYears;

    @ElementCollection
    @CollectionTable(
        name = "policy_plan_benefits",
        joinColumns = @JoinColumn(name = "policy_plan_id")
    )
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "policy_plan_exclusions",
        joinColumns = @JoinColumn(name = "policy_plan_id")
    )
    @Column(name = "exclusion")
    private List<String> exclusions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyPlanStatus status = PolicyPlanStatus.ACTIVE;

    public PolicyPlan() {
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

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public Double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(Double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public Integer getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(Integer durationYears) {
        this.durationYears = durationYears;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    public PolicyPlanStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyPlanStatus status) {
        this.status = status;
    }
}