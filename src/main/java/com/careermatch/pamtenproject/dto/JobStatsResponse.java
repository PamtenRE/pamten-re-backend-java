package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class JobStatsResponse {
    @Min(value = 0, message = "Total jobs cannot be negative")
    private long totalJobs;

    @Min(value = 0, message = "Active jobs cannot be negative")
    private long activeJobs;

    @Min(value = 0, message = "Inactive jobs cannot be negative")
    private long inactiveJobs;

    @Min(value = 0, message = "Recent jobs cannot be negative")
    private long recentJobs;

    @NotBlank(message = "Organization name is required")
    private String organizationName;

    @NotBlank(message = "Employer number is required")
    private String employerNumber;
}