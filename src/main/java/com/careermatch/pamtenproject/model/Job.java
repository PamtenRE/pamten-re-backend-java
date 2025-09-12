package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer jobId;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    @NotNull(message = "Employer is required")
    private Employer employer;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "job_type", nullable = false)
    @NotBlank(message = "Job type is required")
    @Size(max = 50, message = "Job type cannot exceed 50 characters")
    private String jobType;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Job title is required")
    @Size(min = 5, max = 255, message = "Job title must be between 5 and 255 characters")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    @NotBlank(message = "Job description is required")
    @Size(min = 50, message = "Job description must be at least 50 characters")
    private String description;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    @NotBlank(message = "Required skills are required")
    private String requiredSkills;

    @Column(name = "posted_date")
    @NotNull(message = "Posted date is required")
    @PastOrPresent(message = "Posted date cannot be in the future")
    private LocalDate postedDate;

    @Column(name = "posted_by")
    @Size(max = 255, message = "Posted by cannot exceed 255 characters")
    private String postedBy;

    @Column(name = "bill_rate", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Bill rate must be greater than 0")
    private BigDecimal billRate;

    @Column(name = "duration_months")
    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 60, message = "Duration cannot exceed 60 months")
    private Integer durationMonths;

    @Column(name = "is_active")
    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    @Column(name = "created_at")
    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "JobIndustries",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "industry_id")
    )
    private Set<Industry> industries;
}