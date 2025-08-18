package com.careermatch.pamtenproject.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class ResumeRequest {
    @NotNull(message = "setAsDefault flag is required")
    private Boolean setAsDefault; // true if this should be the default resume

    @Size(max = 255, message = "Custom name cannot exceed 255 characters")
    private String customName;    // user-defined name for the resume (optional)
    // Add more fields if needed
}