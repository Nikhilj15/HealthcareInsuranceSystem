package com.healthcare.healthcare_insurance_api.dto;

public class PolicyApplyRequest {

    private Long planId;

    public PolicyApplyRequest() {
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}