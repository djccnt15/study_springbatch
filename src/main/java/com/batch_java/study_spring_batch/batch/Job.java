package com.batch_java.study_spring_batch.batch;

import com.batch_java.study_spring_batch.batch.business.*;
import com.batch_java.study_spring_batch.batch.enums.BatchStatus;
import com.batch_java.study_spring_batch.batch.model.JobExecution;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
public class Job {
    
    private final Tasklet tasklet;
    private final JobExecutionListener jobExecutionListener;
    
    @Builder
    public Job (ItemReader<?> itemReader, ItemProcessor<?, ?> itemProcessor, ItemWriter<?> itemWriter, JobExecutionListener jobExecutionListener) {
        this(new SimpleTasklet(itemReader, itemProcessor, itemWriter), jobExecutionListener);
    }
    
    public JobExecution execute() {
        
        var jobExecuteResult = JobExecution.builder()
            .status(BatchStatus.STARTING)
            .startTime(LocalDateTime.now())
            .build();
        
        jobExecutionListener.beforeJob(jobExecuteResult);
        
        String error = "";
        
        try {
            tasklet.execute();
            jobExecuteResult.setStatus(BatchStatus.COMPLETED);
        } catch (Exception e) {
            jobExecuteResult.setStatus(BatchStatus.FAILED);
            error = e.toString();
        }
        
        jobExecuteResult.setEndTime(LocalDateTime.now());
        
        if (!Objects.equals(error, "")) {
            jobExecutionListener.afterJob(jobExecuteResult);
        } else {
            jobExecutionListener.afterJob(jobExecuteResult, error);
        }
        
        return jobExecuteResult;
    }
}
