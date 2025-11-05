package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ApplicationApplyResponse {
    private Long id;                 
    private Long jobId;
    private String candidateId;      
    private String status;
    private LocalDate appliedDate;
    private String notes;
    private Long resumeId;
    private String coverLetter;
}
