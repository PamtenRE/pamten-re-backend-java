package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Employers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employer_id")
    private Integer employerId;

    @Column(name = "employer_number", nullable = false, unique = true)
    @NotBlank(message = "Employer number is required")
    @Pattern(regexp = "^\\d{5}$", message = "Employer number must be exactly 5 digits")
    private String employerNumber;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @NotNull(message = "User is required")
    private User user;

    @Column(name = "hr_name", nullable = false)
    @NotBlank(message = "HR name is required")
    @Size(min = 2, max = 255, message = "HR name must be between 2 and 255 characters")
    private String hrName;

    @Column(name = "hr_email", nullable = false)
    @NotBlank(message = "HR email is required")
    @Email(message = "Please provide a valid HR email address")
    @Size(max = 255, message = "HR email cannot exceed 255 characters")
    private String hrEmail;

    @ManyToOne
    @JoinColumn(name = "gender_id", referencedColumnName = "gender_id")
    private Gender gender;

    @Column(name = "organization_name", nullable = false)
    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 255, message = "Organization name must be between 2 and 255 characters")
    private String organizationName;

    @Column(name = "end_client")
    @Size(max = 255, message = "End client name cannot exceed 255 characters")
    private String endClient;

    @Column(name = "vendor_name")
    @Size(max = 255, message = "Vendor name cannot exceed 255 characters")
    private String vendorName;

    @Column(name = "created_at")
    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;
}