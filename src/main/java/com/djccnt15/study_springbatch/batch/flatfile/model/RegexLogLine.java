package com.djccnt15.study_springbatch.batch.flatfile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegexLogLine {
    
    private String threadNum;
    
    private String message;
}
