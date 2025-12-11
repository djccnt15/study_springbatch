package com.djccnt15.study_springbatch.batch.practice.log.model;

import com.djccnt15.study_springbatch.batch.practice.log.LogLevelEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessedLogEntry {
    
    private LocalDateTime dateTime;
    
    private LogLevelEnum level;
    
    private String message;
    
    private String errorCode;
    
}
