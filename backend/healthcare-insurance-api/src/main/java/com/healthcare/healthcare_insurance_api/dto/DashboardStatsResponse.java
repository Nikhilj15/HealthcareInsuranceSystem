package com.healthcare.healthcare_insurance_api.dto;

public class DashboardStatsResponse {

    private long totalCustomers;

    private long totalPolicies;

    private long activePolicies;

    private long pendingClaims;

    private long approvedClaims;

    private long rejectedClaims;


    public DashboardStatsResponse() {
    }


    public DashboardStatsResponse(

            long totalCustomers,

            long totalPolicies,

            long activePolicies,

            long pendingClaims,

            long approvedClaims,

            long rejectedClaims) {

        this.totalCustomers = totalCustomers;

        this.totalPolicies = totalPolicies;

        this.activePolicies = activePolicies;

        this.pendingClaims = pendingClaims;

        this.approvedClaims = approvedClaims;

        this.rejectedClaims = rejectedClaims;
    }


    public long getTotalCustomers() {

        return totalCustomers;
    }


    public void setTotalCustomers(
            long totalCustomers) {

        this.totalCustomers = totalCustomers;
    }


    public long getTotalPolicies() {

        return totalPolicies;
    }


    public void setTotalPolicies(
            long totalPolicies) {

        this.totalPolicies = totalPolicies;
    }


    public long getActivePolicies() {

        return activePolicies;
    }


    public void setActivePolicies(
            long activePolicies) {

        this.activePolicies = activePolicies;
    }


    public long getPendingClaims() {

        return pendingClaims;
    }


    public void setPendingClaims(
            long pendingClaims) {

        this.pendingClaims = pendingClaims;
    }


    public long getApprovedClaims() {

        return approvedClaims;
    }


    public void setApprovedClaims(
            long approvedClaims) {

        this.approvedClaims = approvedClaims;
    }


    public long getRejectedClaims() {

        return rejectedClaims;
    }


    public void setRejectedClaims(
            long rejectedClaims) {

        this.rejectedClaims = rejectedClaims;
    }
}