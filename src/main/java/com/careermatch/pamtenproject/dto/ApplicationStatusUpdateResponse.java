package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationStatusUpdateResponse {
    private Long applicationId;
    private String status;
    private LocalDateTime updatedAt;
    private String message;
}
