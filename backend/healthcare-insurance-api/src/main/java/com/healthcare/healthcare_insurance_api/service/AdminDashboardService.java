package com.healthcare.healthcare_insurance_api.service;

import com.healthcare.healthcare_insurance_api.dto.DashboardStatsResponse;
import com.healthcare.healthcare_insurance_api.dto.RecentActivityResponse;
import com.healthcare.healthcare_insurance_api.entity.Claim;
import com.healthcare.healthcare_insurance_api.entity.Policy;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.ClaimStatus;
import com.healthcare.healthcare_insurance_api.enums.PolicyPlanStatus;
import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.repository.ClaimRepository;
import com.healthcare.healthcare_insurance_api.repository.PolicyPlanRepository;
import com.healthcare.healthcare_insurance_api.repository.PolicyRepository;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;

    private final PolicyRepository policyRepository;

    private final PolicyPlanRepository policyPlanRepository;

    private final ClaimRepository claimRepository;


    public AdminDashboardService(
            UserRepository userRepository,
            PolicyRepository policyRepository,
            PolicyPlanRepository policyPlanRepository,
            ClaimRepository claimRepository) {

        this.userRepository = userRepository;

        this.policyRepository = policyRepository;

        this.policyPlanRepository = policyPlanRepository;

        this.claimRepository = claimRepository;
    }


    // =========================================================
    // DASHBOARD STATISTICS
    // =========================================================

    public DashboardStatsResponse getDashboardStats() {

        // -----------------------------------------------------
        // TOTAL REGISTERED CUSTOMERS
        // -----------------------------------------------------

        long totalCustomers =
                userRepository.countByRole(Role.USER);


        // -----------------------------------------------------
        // TOTAL POLICY PLANS
        //
        // IMPORTANT:
        // This count comes from policy_plans table.
        // It does NOT count purchased policies from policies table.
        // -----------------------------------------------------

        long totalPolicies =
                policyPlanRepository.count();


        // -----------------------------------------------------
        // ACTIVE POLICY PLANS
        //
        // This counts only records where policy_plans.status
        // is ACTIVE.
        // -----------------------------------------------------

        long activePolicies =
                policyPlanRepository.countByStatus(
                        PolicyPlanStatus.ACTIVE
                );


        // -----------------------------------------------------
        // PENDING CLAIMS
        // -----------------------------------------------------

        long pendingClaims =
                claimRepository.countByStatus(
                        ClaimStatus.PENDING
                );


        // -----------------------------------------------------
        // APPROVED CLAIMS
        // -----------------------------------------------------

        long approvedClaims =
                claimRepository.countByStatus(
                        ClaimStatus.APPROVED
                );


        // -----------------------------------------------------
        // REJECTED CLAIMS
        // -----------------------------------------------------

        long rejectedClaims =
                claimRepository.countByStatus(
                        ClaimStatus.REJECTED
                );


        // -----------------------------------------------------
        // RETURN DASHBOARD STATISTICS
        // -----------------------------------------------------

        return new DashboardStatsResponse(

                totalCustomers,

                totalPolicies,

                activePolicies,

                pendingClaims,

                approvedClaims,

                rejectedClaims
        );
    }


    // =========================================================
    // RECENT ACTIVITY
    // =========================================================

    public List<RecentActivityResponse> getRecentActivities() {

        List<RecentActivityResponse> activities =
                new ArrayList<>();


        // -----------------------------------------------------
        // 1. NEW CUSTOMER REGISTRATIONS
        // -----------------------------------------------------

        List<User> users =
                userRepository.findTop10ByRoleOrderByCreatedAtDesc(
                        Role.USER
                );


        for (User user : users) {

            if (user.getCreatedAt() == null) {
                continue;
            }


            activities.add(
                    new RecentActivityResponse(

                            "New customer registered",

                            user.getFullName()
                                    + " registered as a new customer.",

                            getRelativeTime(
                                    user.getCreatedAt()
                            ),

                            user.getCreatedAt()
                    )
            );
        }


        // -----------------------------------------------------
        // 2. POLICY PURCHASES
        // -----------------------------------------------------

        List<Policy> policies =
                policyRepository
                        .findTop10ByOrderByAppliedAtDesc();


        for (Policy policy : policies) {

            if (policy.getAppliedAt() == null) {
                continue;
            }


            String customerName =
                    policy.getUser() != null
                            ? policy.getUser().getFullName()
                            : "Customer";


            String planName =
                    policy.getPlan() != null
                            ? policy.getPlan().getName()
                            : "Insurance Plan";


            activities.add(
                    new RecentActivityResponse(

                            "Policy purchased",

                            customerName
                                    + " purchased "
                                    + planName
                                    + ".",

                            getRelativeTime(
                                    policy.getAppliedAt()
                            ),

                            policy.getAppliedAt()
                    )
            );


            // -------------------------------------------------
            // POLICY APPROVED
            // -------------------------------------------------

            if (policy.getApprovedAt() != null) {

                activities.add(
                        new RecentActivityResponse(

                                "Policy approved",

                                "Policy "
                                        + policy.getPolicyNumber()
                                        + " was approved.",

                                getRelativeTime(
                                        policy.getApprovedAt()
                                ),

                                policy.getApprovedAt()
                        )
                );
            }


            // -------------------------------------------------
            // POLICY REJECTED
            // -------------------------------------------------

            if (policy.getRejectedAt() != null) {

                activities.add(
                        new RecentActivityResponse(

                                "Policy rejected",

                                "Policy "
                                        + policy.getPolicyNumber()
                                        + " was rejected.",

                                getRelativeTime(
                                        policy.getRejectedAt()
                                ),

                                policy.getRejectedAt()
                        )
                );
            }
        }


        // -----------------------------------------------------
        // 3. CLAIM ACTIVITIES
        // -----------------------------------------------------

        List<Claim> claims =
                claimRepository
                        .findTop10ByOrderByClaimDateDesc();


        for (Claim claim : claims) {

            if (claim.getClaimDate() == null) {
                continue;
            }


            String customerName =
                    claim.getUser() != null
                            ? claim.getUser().getFullName()
                            : "Customer";


            // -------------------------------------------------
            // CLAIM SUBMISSION TIME
            // -------------------------------------------------

            LocalDateTime claimSubmittedAt =
                    claim.getClaimDate()
                            .atStartOfDay();


            activities.add(
                    new RecentActivityResponse(

                            "Claim submitted",

                            customerName
                                    + " submitted claim "
                                    + claim.getClaimNumber()
                                    + ".",

                            getRelativeTime(
                                    claimSubmittedAt
                            ),

                            claimSubmittedAt
                    )
            );


            // -------------------------------------------------
            // CLAIM APPROVED
            // -------------------------------------------------

            if (claim.getApprovedAt() != null) {

                activities.add(
                        new RecentActivityResponse(

                                "Claim approved",

                                "Claim "
                                        + claim.getClaimNumber()
                                        + " was approved.",

                                getRelativeTime(
                                        claim.getApprovedAt()
                                ),

                                claim.getApprovedAt()
                        )
                );
            }


            // -------------------------------------------------
            // CLAIM REJECTED
            // -------------------------------------------------

            if (claim.getRejectedAt() != null) {

                activities.add(
                        new RecentActivityResponse(

                                "Claim rejected",

                                "Claim "
                                        + claim.getClaimNumber()
                                        + " was rejected.",

                                getRelativeTime(
                                        claim.getRejectedAt()
                                ),

                                claim.getRejectedAt()
                        )
                );
            }
        }


        // -----------------------------------------------------
        // SORT LATEST ACTIVITY FIRST
        // -----------------------------------------------------

        activities.sort(
                Comparator.comparing(
                        RecentActivityResponse::getActivityAt,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );


        // -----------------------------------------------------
        // RETURN ONLY LATEST 6 ACTIVITIES
        // -----------------------------------------------------

        if (activities.size() > 6) {

            return new ArrayList<>(
                    activities.subList(0, 6)
            );
        }


        return activities;
    }


    // =========================================================
    // RELATIVE TIME
    // =========================================================

    private String getRelativeTime(
            LocalDateTime activityTime) {

        LocalDateTime now =
                LocalDateTime.now();


        if (activityTime.isAfter(now)) {

            return "just now";
        }


        Duration duration =
                Duration.between(
                        activityTime,
                        now
                );


        long minutes =
                duration.toMinutes();


        if (minutes < 1) {

            return "just now";
        }


        if (minutes < 60) {

            return minutes
                    + (minutes == 1
                    ? " minute ago"
                    : " minutes ago");
        }


        long hours =
                duration.toHours();


        if (hours < 24) {

            return hours
                    + (hours == 1
                    ? " hour ago"
                    : " hours ago");
        }


        long days =
                duration.toDays();


        if (days < 30) {

            return days
                    + (days == 1
                    ? " day ago"
                    : " days ago");
        }


        return activityTime
                .toLocalDate()
                .toString();
    }
}