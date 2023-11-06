package com.batch_sample.batchjava.batch.model;

import com.batch_sample.batchjava.batch.enums.BatchStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class JobExecution {

    private BatchStatus status;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
}
