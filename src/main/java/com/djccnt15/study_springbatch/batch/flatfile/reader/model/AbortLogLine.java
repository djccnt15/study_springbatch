package com.djccnt15.study_springbatch.batch.flatfile.reader.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class AbortLogLine extends SystemLogLine {
    
    private String application;
    
    private String errorType;
    
    private String message;
    
    private String exitCode;
    
    private String processPath;
    
    private String status;
}
