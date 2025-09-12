package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer candidateId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @NotNull(message = "User is required")
    private User user;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @Column(name = "experience_years")
    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 50, message = "Experience years cannot exceed 50")
    private Integer experienceYears;

    @Column(name = "linkedin_url")
    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*", message = "Invalid LinkedIn URL format")
    @Size(max = 500, message = "LinkedIn URL cannot exceed 500 characters")
    private String linkedinUrl;

    @Column(name = "github_username")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Invalid GitHub username format")
    @Size(max = 100, message = "GitHub username cannot exceed 100 characters")
    private String githubUsername;

    @Column(name = "created_at")
    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;
}