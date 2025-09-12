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
@Table(name = "Resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Integer resumeId;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @NotNull(message = "Candidate is required")
    private Candidate candidate;

    @Column(name = "file_name", nullable = false)
    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name cannot exceed 255 characters")
    private String fileName;

    @Column(name = "file_path", nullable = false)
    @NotBlank(message = "File path is required")
    @Size(max = 500, message = "File path cannot exceed 500 characters")
    private String filePath; // GCS URL or path

    @Column(name = "file_size")
    @Min(value = 1, message = "File size must be positive")
    private Long fileSize;

    @Column(name = "upload_date")
    @NotNull(message = "Upload date is required")
    @PastOrPresent(message = "Upload date cannot be in the future")
    private LocalDateTime uploadDate;

    @Column(name = "is_active")
    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @Column(name = "is_default")
    @NotNull(message = "Default status is required")
    private Boolean isDefault; // <-- Add this

    @Column(name = "custom_name")
    @Size(max = 255, message = "Custom name cannot exceed 255 characters")
    private String customName;
}