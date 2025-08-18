package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
public class ResumeResponse {
    @NotNull(message = "Resume ID is required")
    @Min(value = 1, message = "Resume ID must be a positive number")
    private Integer resumeId;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File path is required")
    private String filePath;

    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be positive")
    private Long fileSize;

    private String customName;

    @NotNull(message = "Default status is required")
    private Boolean isDefault;

    @NotNull(message = "Upload date is required")
    @PastOrPresent(message = "Upload date cannot be in the future")
    private LocalDateTime uploadDate;
}