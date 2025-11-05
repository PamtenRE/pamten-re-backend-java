package com.careermatch.pamtenproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStageResponse {
    private String stage;           
    private boolean completed;      
    private LocalDateTime date;     
}
