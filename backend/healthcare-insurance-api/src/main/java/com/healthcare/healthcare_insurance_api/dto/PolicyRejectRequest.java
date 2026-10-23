package com.healthcare.healthcare_insurance_api.dto;

public class PolicyRejectRequest {

    private String reason;

    public PolicyRejectRequest() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}