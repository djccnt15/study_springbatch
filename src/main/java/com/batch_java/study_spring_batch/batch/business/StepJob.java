package com.batch_java.study_spring_batch.batch.business;

import com.batch_java.study_spring_batch.batch.enums.BatchStatus;
import com.batch_java.study_spring_batch.batch.model.JobExecution;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class StepJob implements Job {
    
    private final List<Step> stepList;
    private final JobExecutionListener jobExecutionListener;
    
    @Override
    public JobExecution execute() {
        
        var jobExecuteResult = JobExecution.builder()
            .status(BatchStatus.STARTING)
            .startTime(LocalDateTime.now())
            .build();
        
        jobExecutionListener.beforeJob(jobExecuteResult);
        
        String error = "";
        
        try {
            stepList.forEach(Step::execute);
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
