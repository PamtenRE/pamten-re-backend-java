package com.careermatch.pamtenproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyRequest {
    private Long jobId;
    private Long candidateId;
    private Long resumeId;
    private String notes;
    private String coverLetter;
}
