package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobStatsResponse {
    private long totalJobs;
    private long activeJobs;
    private long inactiveJobs;
    private long recentJobs;
    private String organizationName;
    private String employerNumber;
}