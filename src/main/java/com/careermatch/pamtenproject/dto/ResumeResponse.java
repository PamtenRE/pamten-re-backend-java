package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ResumeResponse {
    private Integer resumeId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String customName;
    private Boolean isDefault;
    private LocalDateTime uploadDate;
}