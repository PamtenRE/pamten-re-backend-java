package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "JobSpecificResumes", schema = "db_owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSpecificResume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_specific_resume_id")
    private Integer jobSpecificResumeId;

    @ManyToOne
    @JoinColumn(name = "job_application_id", nullable = false)
    @NotNull(message = "Job application is required")
    private JobApplication jobApplication;

    @ManyToOne
    @JoinColumn(name = "base_resume_id", nullable = false)
    @NotNull(message = "Base resume is required")
    private Resume baseResume;

    @Column(name = "file_name", nullable = false)
    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name cannot exceed 255 characters")
    private String fileName;

    @Column(name = "file_path", nullable = false)
    @NotBlank(message = "File path is required")
    @Size(max = 500, message = "File path cannot exceed 500 characters")
    private String filePath; // GCS URL for the job-specific resume

    @Column(name = "file_size")
    @Min(value = 1, message = "File size must be positive")
    private Long fileSize;

    @Column(name = "job_title", nullable = false)
    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Job title cannot exceed 255 characters")
    private String jobTitle;

    @Column(name = "job_id", nullable = false)
    @NotNull(message = "Job ID is required")
    private Integer jobId;

    @Column(name = "processing_status", nullable = false)
    @NotBlank(message = "Processing status is required")
    private String processingStatus; // PROCESSING, COMPLETED, FAILED

    @Column(name = "ai_model_version")
    private String aiModelVersion;

    @Column(name = "processing_notes", columnDefinition = "TEXT")
    private String processingNotes;

    @Column(name = "created_date", nullable = false)
    @NotNull(message = "Created date is required")
    @PastOrPresent(message = "Created date cannot be in the future")
    private LocalDateTime createdDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (processingStatus == null) {
            processingStatus = "PROCESSING";
        }
    }
}

