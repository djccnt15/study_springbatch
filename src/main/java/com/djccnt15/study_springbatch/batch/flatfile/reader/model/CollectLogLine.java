package com.djccnt15.study_springbatch.batch.flatfile.reader.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class CollectLogLine extends SystemLogLine {
    
    private String dumpType;
    
    private String processId;
    
    private String dumpPath;
}
