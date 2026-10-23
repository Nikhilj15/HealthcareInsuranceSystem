package com.healthcare.healthcare_insurance_api.dto;

public class ClaimRejectRequest {

    private String reason;

    public ClaimRejectRequest() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}