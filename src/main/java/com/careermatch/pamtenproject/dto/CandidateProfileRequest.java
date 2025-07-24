package com.careermatch.pamtenproject.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CandidateProfileRequest {
    private String userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer genderId;         // Accept genderId from frontend
    private Integer experienceYears;
    private String linkedinUrl;
    private String githubUsername;
}