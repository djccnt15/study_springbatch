package com.djccnt15.study_springbatch.batch.multithread.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TargetPriorityResult {
    
    private Long humanId;
    
    private String humanName;
    
    private PriorityEnum priorityEnum;
    
    private int threatScore;
    
    private double severityIndex;
    
    private int activityCount;
}
