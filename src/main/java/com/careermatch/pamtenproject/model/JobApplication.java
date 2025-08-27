package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "JobApplications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    @NotNull(message = "Job is required")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    @NotNull(message = "Candidate is required")
    private Candidate candidate;

    @Column(name = "application_date", nullable = false)
    @NotNull(message = "Application date is required")
    private LocalDateTime applicationDate;

    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private String status; // PENDING, REVIEWING, ACCEPTED, REJECTED

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (applicationDate == null) {
            applicationDate = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

