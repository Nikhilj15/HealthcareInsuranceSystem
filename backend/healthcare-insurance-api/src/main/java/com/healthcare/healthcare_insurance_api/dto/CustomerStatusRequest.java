package com.healthcare.healthcare_insurance_api.dto;

import com.healthcare.healthcare_insurance_api.enums.UserStatus;

public class CustomerStatusRequest {

    private UserStatus status;

    public CustomerStatusRequest() {
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}