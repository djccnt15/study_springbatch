package com.djccnt15.study_springbatch.batch.flatfile.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class SystemLogLine {
    
    private String type;
    
    private String timestamp;
}
