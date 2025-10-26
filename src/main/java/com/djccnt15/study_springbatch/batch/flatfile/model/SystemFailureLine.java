package com.djccnt15.study_springbatch.batch.flatfile.model;

import lombok.Data;

@Data
public class SystemFailureLine {
    
    private String errorId;
    
    private String errorDateTime;
    
    private String severity;
    
    private Integer processId;
    
    private String errorMessage;
}
