package com.careermatch.pamtenproject.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class JobApplicationRequest {
    @NotNull(message = "Job ID is required")
    @Min(value = 1, message = "Job ID must be a positive number")
    private Integer jobId;

    @NotNull(message = "Resume ID is required")
    @Min(value = 1, message = "Resume ID must be a positive number")
    private Integer resumeId;

    private String notes; // Optional application notes
}

