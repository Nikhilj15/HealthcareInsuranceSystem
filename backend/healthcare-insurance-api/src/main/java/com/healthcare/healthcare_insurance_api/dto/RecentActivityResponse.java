package com.healthcare.healthcare_insurance_api.dto;

import java.time.LocalDateTime;

public class RecentActivityResponse {

    private String title;

    private String description;

    private String time;

    private LocalDateTime activityAt;

    public RecentActivityResponse() {
    }

    public RecentActivityResponse(
            String title,
            String description,
            String time,
            LocalDateTime activityAt) {

        this.title = title;
        this.description = description;
        this.time = time;
        this.activityAt = activityAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocalDateTime getActivityAt() {
        return activityAt;
    }

    public void setActivityAt(LocalDateTime activityAt) {
        this.activityAt = activityAt;
    }
}