package com.djccnt15.study_springbatch.batch.itemprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandReport {
    
    private Long executorId;
    
    private String action;
    
    private String severity;
    
    private LocalDateTime timestamp;
}
