package com.djccnt15.study_springbatch.batch.composite.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog {
    
    private String type;  // CRITICAL or NORMAL
    
    private String message;
    
    private int cpuUsage;
    
    private long memoryUsage;
}
