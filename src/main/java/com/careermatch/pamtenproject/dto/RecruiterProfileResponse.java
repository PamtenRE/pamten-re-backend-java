package com.careermatch.pamtenproject.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterProfileResponse {
    private String userId;
    private String fullName;
    private String email;
    private String roleName;
    private boolean profileCompleted;

    // Employer details
    private String employerNumber;
    private String organizationName;
    private String hrName;
    private String hrEmail;
    private String endClient;
    private String vendorName;

    // Personal details
    private LocalDate dateOfBirth;
    private String gender;

    // Industry
    private String industryName;
    private String industryDescription;

    private String message;
}
