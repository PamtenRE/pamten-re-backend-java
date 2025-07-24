package com.careermatch.pamtenproject.dto;

import lombok.Data;

@Data
public class ResumeRequest {
    private Boolean setAsDefault; // true if this should be the default resume
    private String customName;    // user-defined name for the resume (optional)
    // Add more fields as needed
}