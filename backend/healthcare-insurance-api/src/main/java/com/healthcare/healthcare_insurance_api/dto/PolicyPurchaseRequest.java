package com.healthcare.healthcare_insurance_api.dto;

public class PolicyPurchaseRequest {

    private Long planId;

    public PolicyPurchaseRequest() {
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}