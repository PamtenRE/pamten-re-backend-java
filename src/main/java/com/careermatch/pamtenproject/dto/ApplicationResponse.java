package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;


@Data
@Builder
public class ApplicationResponse {
    private Long id;                 
    private Long jobId;
    private String candidateId;      
    private String status;
    private LocalDate appliedDate;
    private String notes;
    private Long resumeId;
    private String coverLetter;
    private String company;
    private String position;
    private String salaryRange;
    private String department;
    private String location;

    private List<ApplicationStageResponse> applicationStages;

}
