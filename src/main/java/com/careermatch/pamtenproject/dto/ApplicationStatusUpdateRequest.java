package com.careermatch.pamtenproject.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplicationStatusUpdateRequest {
    private String status; 
    private List<StageUpdate> stageUpdates; 
    private String notes; 

    @Data
    public static class StageUpdate {
        private String stage;
        private boolean completed;
        private LocalDateTime date;
    }
}
