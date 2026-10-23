package com.healthcare.healthcare_insurance_api.controller;

import com.healthcare.healthcare_insurance_api.dto.DashboardStatsResponse;
import com.healthcare.healthcare_insurance_api.dto.RecentActivityResponse;
import com.healthcare.healthcare_insurance_api.service.AdminDashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;


    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {

        this.adminDashboardService =
                adminDashboardService;
    }


    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse>
    getDashboardStats() {

        return ResponseEntity.ok(
                adminDashboardService.getDashboardStats()
        );
    }


    @GetMapping("/recent-activities")
    public ResponseEntity<List<RecentActivityResponse>>
    getRecentActivities() {

        return ResponseEntity.ok(
                adminDashboardService.getRecentActivities()
        );
    }
}