package com.djccnt15.study_springbatch.batch.practice.log.model;

import lombok.Data;

@Data
public class LogEntry {
    
    private String dateTime;
    
    private String level;
    
    private String message;
}
